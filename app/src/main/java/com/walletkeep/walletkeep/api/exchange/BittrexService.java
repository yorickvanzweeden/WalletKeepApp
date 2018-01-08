package com.walletkeep.walletkeep.api.exchange;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.CurrencyTickerCorrection;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.entity.Asset;

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
        super.fetch();

        // Get signature
        long timestamp = System.currentTimeMillis();
        String data = String.format("https://bittrex.com/api/v1.1/account/getbalances?apikey=%s&nonce=%s", ec.getKey(), timestamp);

        String signature = sg.bytesToHex(
                sg.hMac(
                        sg.getBytes(data),
                        sg.getBytes(ec.getSecret()),
                        "HmacSHA512")
        );

        // Create request
        BittrexApi api = RetrofitClient.getClient("https://bittrex.com").create(BittrexApi.class);
        Call<BittrexResponse> responseCall = api.getBalance(
                signature, ec.getKey(), timestamp
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
    public class BittrexResponse extends AbstractResponse {

        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("result")
        @Expose
        private List<Result> result = null;

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

        Asset getAsset(int walletId) {
            return new Asset(walletId, CurrencyTickerCorrection.correct(currency), Float.parseFloat(balance));
        }
    }
}
