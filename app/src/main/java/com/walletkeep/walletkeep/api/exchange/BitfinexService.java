package com.walletkeep.walletkeep.api.exchange;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.CurrencyTickerCorrection;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.entity.Asset;

import org.json.JSONException;
import org.json.JSONObject;

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
        super.fetch();
        String baseUrl = "https://api.bitfinex.com/";
        // Get signature
        JSONObject jo = new JSONObject();
        try {
            jo.put("request", "/v1/balances");
            jo.put("nonce", Long.toString(System.currentTimeMillis()));
        } catch (JSONException e) {
            this.responseHandler.returnError("Error while creating a Bitfinex request.");
            return;
        }

        String payload = jo.toString();
        String payload_base64 = sg.encode(sg.getBytes(payload));

        String signature = sg.bytesToHex(
                sg.hMac(
                        sg.getBytes(payload_base64),
                        sg.getBytes(ec.getSecret()),
                        "HmacSHA384")
        );

        // Create request
        BitfinexApi api = RetrofitClient.getClient(baseUrl).create(BitfinexApi.class);
        Call<List<BitfinexResponse>> responseCall = api.getBalance(
                ec.getKey(), payload_base64, signature, payload
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
    public class BitfinexResponse extends AbstractResponse {

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
