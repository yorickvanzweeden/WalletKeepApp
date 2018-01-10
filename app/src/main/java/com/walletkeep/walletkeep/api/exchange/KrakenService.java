package com.walletkeep.walletkeep.api.exchange;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;
import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.CurrencyTickerCorrection;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.entity.Asset;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class KrakenService extends ApiService {

    @Override
    public void fetch() {
        super.fetch();
        String baseUrl = "https://api.kraken.com";
        // Get signature
        long timestamp = System.currentTimeMillis();
        String postData = String.format("%snonce=%s", timestamp, timestamp);
        String url = "/0/private/Balance";
        byte[] data = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed_data = digest.digest(sg.getBytes(postData));
            String b = sg.encode(hashed_data);
            String a = sg.encode(sg.getBytes(url));

            data = sg.decode(a.concat(b));
        } catch (NoSuchAlgorithmException e) {
            this.responseHandler.returnError("SHA-256 not supported"); return;
        }

        String signature = sg.encode(sg.hMac(data, sg.decode(ec.getSecret()), "HmacSHA512"));

        // Create request
        KrakenApi api = RetrofitClient.getClient(baseUrl).create(KrakenApi.class);
        Call<KrakenResponse> responseCall = api.getBalance(
                ec.getKey(), signature, timestamp
        );

        // Perform request
        performRequest(responseCall);
    }

    /**
     * Retrofit request interfaces
     */
    private interface KrakenApi {
        //@Headers("Content-Type: application/json")
        @FormUrlEncoded
        @POST("/0/private/Balance")
        Call<KrakenResponse> getBalance(
                @Header("API-Key") String key,
                @Header("API-Sign") String signature,
                @Field("nonce") long timestamp
        );
    }

    /**
     * POJO used for converting the JSON response to Java
     */
    public class KrakenResponse extends AbstractResponse {
        @SerializedName("error")
        @Expose
        private List<String> error;

        @SerializedName("result")
        @Expose
        private Object result;

        @Override
        public ArrayList<Asset> getAssets(int walletId) {
            ArrayList<Asset> assets = new ArrayList<Asset>();

            // Convert object to LinkedTreeMap as the keys are not identical
            LinkedTreeMap x = (LinkedTreeMap)result;

            // If no keys, thus no assets
            if (x.size() == 0) { return assets; }

            // Convert entrySet to assets
            try {
                for (Object y: x.entrySet()) {
                    Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) y;
                    assets.add(new Asset(
                            walletId,
                            CurrencyTickerCorrection.correct(
                                    ((String)entry.getKey()).substring(1)
                            ),
                            Float.parseFloat((String)entry.getValue())
                    ));
                }
            } catch (Exception e) {
                // On error, set the error that will be picked up in handleError() later on
                error.set(0, "Could not convert the response to assets");
                return null;
            }


            return assets;
        }

        @Override
        public String handleError() {
            return this.error.get(0);
        }
    }
}
