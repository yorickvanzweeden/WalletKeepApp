package com.walletkeep.walletkeep.api.exchange;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.CurrencyTickerCorrection;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.entity.Asset;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;


public class BittrexService extends ApiService {
    @Override
    public void fetch() {
        String key;
        try { key = ec.getKey(); } catch (NullPointerException e) {
            this.returnError("No credentials have been provided.");
            return;
        }

        // Get signature
        long timestamp = System.currentTimeMillis();
        String data = String.format("https://bittrex.com/api/v1.1/account/getbalances?apikey=%s&nonce=%s", key, timestamp);
        String signature;

        // In case of invalid secret
        try { signature = generateSignature(data.getBytes("UTF-8"), ec.getSecret(), false, "HmacSHA512"); }
        catch (IllegalArgumentException e) { this.returnError(e.getMessage()); return; }
        catch (NullPointerException e) { this.returnError("No credentials have been provided."); return; }
        catch (UnsupportedEncodingException e) { this.returnError("Encoding UTF-8 not supported"); return; }

        // Create request
        BittrexApi api = RetrofitClient.getClient("https://bittrex.com").create(BittrexApi.class);
        Call<BittrexResponse> responseCall = api.getBalance(
                signature, key, timestamp
        );

        // Perform request
        performRequest(responseCall);
    }

    /**
     * Retrofit request interfaces
     */
    private interface BittrexApi {
        @Headers("Content-Type: application/json")
        @GET("/api/v1.1/account/getbalances")
        Call<BittrexResponse> getBalance(
                @Header("APISIGN") String signature,
                @Query("apikey") String key,
                @Query("nonce") long timestamp
        );
    }

    /**
     * POJO used for converting the JSON response to Java
     */
    public class BittrexResponse extends ApiService.IResponse {

        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("result")
        @Expose
        private List<Result> result = null;

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<Result> getResult() {
            return result;
        }

        public void setResult(List<Result> result) {
            this.result = result;
        }

        @Override
        public ArrayList<Asset> getAssets(int walletId) {
            return new ArrayList<Asset>() {{
                for(Result res:result) {
                    add(res.getAsset(walletId));
                }
            }};
        }

        @Override
        public String handleError() {
            return this.message;
        }
    }

    public class Result {

        @SerializedName("Currency")
        @Expose
        private String currency;
        @SerializedName("Balance")
        @Expose
        private String balance;
        @SerializedName("Available")
        @Expose
        private Double available;
        @SerializedName("Pending")
        @Expose
        private Double pending;
        @SerializedName("CryptoAddress")
        @Expose
        private String cryptoAddress;
        @SerializedName("Requested")
        @Expose
        private Boolean requested;
        @SerializedName("Uuid")
        @Expose
        private Object uuid;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public Double getAvailable() {
            return available;
        }

        public void setAvailable(Double available) {
            this.available = available;
        }

        public Double getPending() {
            return pending;
        }

        public void setPending(Double pending) {
            this.pending = pending;
        }

        public String getCryptoAddress() {
            return cryptoAddress;
        }

        public void setCryptoAddress(String cryptoAddress) {
            this.cryptoAddress = cryptoAddress;
        }

        public Boolean getRequested() {
            return requested;
        }

        public void setRequested(Boolean requested) {
            this.requested = requested;
        }

        public Object getUuid() {
            return uuid;
        }

        public void setUuid(Object uuid) {
            this.uuid = uuid;
        }

        public Asset getAsset(int walletId) {
            return new Asset(walletId, CurrencyTickerCorrection.correct(currency), Float.parseFloat(balance));
        }
    }
}
