package com.walletkeep.walletkeep.api.exchange;

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

public class GDAXService extends ApiService {
    private String baseUrl = "https://api.gdax.com";

    @Override
    public void fetch() {
        // Perform time synchronisations
        GDAXApi api = RetrofitClient.getClient(baseUrl).create(GDAXApi.class);
        Call<TimestampResponse> responseCall = api.getTimestamp();
        responseCall.enqueue(new Callback<TimestampResponse>() {
            @Override
            public void onResponse(Call<TimestampResponse> call, Response<TimestampResponse> response) {
                fetch(response.body().getEpoch().longValue());
            }

            @Override
            public void onFailure(Call<TimestampResponse> call, Throwable t) {
                fetch(System.currentTimeMillis() / 1000);
            }
        });
    }

    public void fetch(Long timestamp) {
        super.fetch();

        // Get signature
        String data =  timestamp + "GET/accounts";

        String signature = sg.encode(
                sg.hMac(
                        sg.getBytes(data),
                        sg.decode(
                                ec.getSecret()
                        ),
                        "HmacSHA256"
                )
        );

        // Create request
        GDAXApi api = RetrofitClient.getClient(baseUrl).create(GDAXApi.class);
        Call<List<GDAXResponse>> responseCall = api.getBalance(
                signature, timestamp, ec.getKey(), ec.getPassphrase()
        );

        // Perform request
        performRequest(responseCall, new ErrorParser("message"));
    }


    /**
     * Retrofit request interfaces
     */
    protected interface GDAXApi {
        @Headers("Content-Type: application/json")
        @GET("/accounts")
        Call<List<GDAXResponse>> getBalance(
                @Header("CB-ACCESS-SIGN") String signature,
                @Header("CB-ACCESS-TIMESTAMP") long timestamp,
                @Header("CB-ACCESS-KEY") String key,
                @Header("CB-ACCESS-PASSPHRASE") String passphrase
        );

        @Headers("Content-Type: application/json")
        @GET("/time")
        Call<TimestampResponse> getTimestamp();
    }

    /**
     * POJO used for converting the JSON response to Java
     */
    private class GDAXResponse extends AbstractResponse {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("currency")
        @Expose
        private String currency;
        @SerializedName("balance")
        @Expose
        private String balance;
        @SerializedName("available")
        @Expose
        private String available;
        @SerializedName("hold")
        @Expose
        private String hold;
        @SerializedName("profile_id")
        @Expose
        private String profileId;

        public Asset getAsset(int walletId){
            return new Asset(walletId, CurrencyTickerCorrection.correct(currency), Float.parseFloat(balance));
        }

        @Override
        public ArrayList<Asset> getAssets(int walletId) {
            return new ArrayList<Asset>() {{
                add(getAsset(walletId));
            }};
        }
    }

    private class TimestampResponse {

        @SerializedName("iso")
        @Expose
        private String iso;
        @SerializedName("epoch")
        @Expose
        private Double epoch;

        public Double getEpoch() {
            return epoch;
        }
    }
}