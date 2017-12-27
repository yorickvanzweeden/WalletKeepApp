package com.walletkeep.walletkeep.api.exchange;

import android.util.Base64;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.CurrencyTickerCorrection;
import com.walletkeep.walletkeep.api.ErrorParser;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.entity.Asset;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

public class KucoinService extends ApiService {
    @Override
    public void fetch() {
        // Get signature
        long timestamp = System.currentTimeMillis() + 43000;
        String data =  "/v1/account/balance/" + timestamp + "/";
        try{
            data = Base64.encodeToString(data.getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (Exception e) {}
        String signature;

        // In case of invalid secret
        try{ signature = generateSignature(data.getBytes("UTF-8"), this.ec.getSecret(), false); }
        catch (IllegalArgumentException e) { this.returnError(e.getMessage()); return; }
        catch (NullPointerException e) { this.returnError("No credentials have been provided."); return; }
        catch (UnsupportedEncodingException e) { this.returnError("Encoding UTF-8 not supported"); return; }

        // Create request
        KucoinApi api = RetrofitClient.getClient("https://api.kucoin.com").create(KucoinApi.class);
        Call<KucoinResponse> responseCall = api.getBalance(
                ec.getKey(), signature, timestamp
        );

        // Perform request
        performRequest(responseCall, new ErrorParser("msg"));
    }

    /**
     * Retrofit request interfaces
     */
    protected interface KucoinApi {
        @Headers("Content-Type: application/json")
        @GET("/v1/account/balance")
        Call<KucoinResponse> getBalance(
                @Header("KC-API-KEY") String key,
                @Header("KC-API-SIGNATURE") String signature,
                @Header("KC-API-NONCE") long timestamp
        );
    }

    /**
     * POJO used for converting the JSON response to Java
     */
    private class KucoinResponse extends IResponse{
        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("code")
        @Expose
        private String code;
        @SerializedName("data")
        @Expose
        private List<Coin> data = null;

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public List<Coin> getData() {
            return data;
        }

        public void setData(List<Coin> data) {
            this.data = data;
        }

        @Override
        public ArrayList<Asset> getAssets(int walletId) {
            ArrayList<Asset> assets = new ArrayList<Asset>();
            for (Coin coin:data) {
                assets.add(coin.getAsset(walletId));
            }
            return assets;
        }
    }

    public class Coin {
        @SerializedName("coinType")
        @Expose
        private String coinType;
        @SerializedName("balance")
        @Expose
        private String balance;
        @SerializedName("freezeBalance")
        @Expose
        private String freezeBalance;

        public String getCoinType() {
            return coinType;
        }

        public void setCoinType(String coinType) {
            this.coinType = coinType;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getFreezeBalance() {
            return freezeBalance;
        }

        public void setFreezeBalance(String freezeBalance) {
            this.freezeBalance = freezeBalance;
        }

        public Asset getAsset(int walletId) {
            return new Asset(walletId, CurrencyTickerCorrection.correct(coinType), Float.parseFloat(balance));
        }
    }
}