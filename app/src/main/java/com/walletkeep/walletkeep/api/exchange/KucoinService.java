package com.walletkeep.walletkeep.api.exchange;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.CurrencyTickerCorrection;
import com.walletkeep.walletkeep.api.ErrorParser;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.entity.Asset;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

public class KucoinService extends ApiService {
    private String baseUrl = "https://api.kucoin.com";

    @Override
    public void fetch() {
        // Perform time synchronisations
        KucoinApi api = RetrofitClient.getClient(baseUrl).create(KucoinApi.class);
        Call<TimestampResponse> responseCall = api.getTimestamp();
        responseCall.enqueue(new Callback<TimestampResponse>() {
            @Override
            public void onResponse(@NonNull Call<TimestampResponse> call,
                                   @NonNull Response<TimestampResponse> response) {
                Long ts = response.body().getTimestamp() + ApiService.slow_device_delay * 1000;
                fetch(ts);
            }

            @Override
            public void onFailure(@NonNull Call<TimestampResponse> call, @NonNull Throwable t) {
                fetch(System.currentTimeMillis());
            }
        });
    }

    public void fetch(Long timestamp) {
        super.fetch();

        // Get signature
        String data =  "/v1/account/balance/" + timestamp + "/";
        data = sg.encode(sg.getBytes(data));

        String signature = sg.bytesToHex(
                sg.hMac(
                        sg.getBytes(data),
                        sg.getBytes(ec.getSecret()),
                        "HmacSHA256")
        );

        // Create request
        KucoinApi api = RetrofitClient.getClient(baseUrl).create(KucoinApi.class);
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

        @Headers("Content-Type: application/json")
        @GET("/v1/open/lang-list")
        Call<TimestampResponse> getTimestamp();
    }

    /**
     * POJO used for converting the JSON response to Java
     */
    private class KucoinResponse extends AbstractResponse {
        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("code")
        @Expose
        private String code;
        @SerializedName("data")
        @Expose
        private List<Coin> data = null;
        @SerializedName("timestamp")
        @Expose
        private Long timestamp;

        public Long getTimestamp() {
            return timestamp;
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

        Asset getAsset(int walletId) {
            return new Asset(
                    walletId,
                    CurrencyTickerCorrection.correct(coinType),
                    Float.parseFloat(balance));
        }
    }

    private class TimestampResponse {
        @SerializedName("timestamp")
        @Expose
        private Long timestamp;

        public Long getTimestamp() {
            return timestamp;
        }
    }

}