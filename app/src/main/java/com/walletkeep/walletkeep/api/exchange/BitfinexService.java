package com.walletkeep.walletkeep.api.exchange;

import android.util.Base64;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.CurrencyTickerCorrection;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.entity.Asset;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public class BitfinexService extends ApiService {
    @Override
    public void fetch() {
        String key;
        try { key = ec.getKey(); } catch (NullPointerException e) {
            this.returnError("No credentials have been provided.");
            return;
        }

        // Get signature
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "/v1/balances");
            jo.put("nonce", Long.toString(System.currentTimeMillis()));
        } catch (JSONException e) {
            this.returnError("Error while creating a Bitfinex request.");
            return;
        }

        String payload = jo.toString();
        String payload_base64 = Base64.encodeToString(payload.getBytes(), Base64.NO_WRAP);
        String signature;

        // In case of invalid secret
        try { signature = generateSignature(payload_base64.getBytes("UTF-8"), ec.getSecret(), false, "HmacSHA384"); }
        catch (IllegalArgumentException e) { this.returnError(e.getMessage()); return; }
        catch (NullPointerException e) { this.returnError("No credentials have been provided."); return; }
        catch (UnsupportedEncodingException e) { this.returnError("Encoding UTF-8 not supported"); return; }


        // Create request
        BitfinexApi api = RetrofitClient.getClient("https://api.bitfinex.com/").create(BitfinexApi.class);
        Call<List<BitfinexResponse>> responseCall = api.getBalance(
                key, payload_base64, signature, payload
        );

        // Perform request
        performRequest(responseCall);
    }

    /**
     * Retrofit request interfaces
     */
    private interface BitfinexApi {
        @Headers("Content-Type: application/json")
        @POST("/v1/balances")
        Call<List<BitfinexResponse>> getBalance(
                @Header("X-BFX-APIKEY") String key,
                @Header("X-BFX-PAYLOAD") String payload,
                @Header("X-BFX-SIGNATURE") String signature,
                @Body String body
        );
    }

    /**
     * POJO used for converting the JSON response to Java
     */
    public class BitfinexResponse extends IResponse {

        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("currency")
        @Expose
        private String currency;
        @SerializedName("amount")
        @Expose
        private String amount;
        @SerializedName("available")
        @Expose
        private String available;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency.toUpperCase();
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getAvailable() {
            return available;
        }

        public void setAvailable(String available) {
            this.available = available;
        }

        private Asset getAsset(int walletId) {
            return new Asset(walletId, CurrencyTickerCorrection.correct(currency), Float.parseFloat(amount));

        }

        @Override
        public ArrayList<Asset> getAssets(int walletId) {
            return new ArrayList<Asset>() {{
                add(getAsset(walletId));
            }};
        }
    }
}
