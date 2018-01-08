package com.walletkeep.walletkeep.api.data;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.entity.CurrencyPrice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class CryptoCompareService {
    private PricesResponseListener listener;
    private String baseUrl = "https://min-api.cryptocompare.com/data/";

    public CryptoCompareService(PricesResponseListener listener) {
        this.listener = listener;
    }

    /**
     * Fetch currency data from CryptoCompare
     */
    public void fetch(List<String> toFetch) {
        // Create request
        CryptoCompareAPI api = RetrofitClient.getClient(baseUrl).create(CryptoCompareAPI.class);

        Call<Map<String, Response>> responseCall = api.getCurrencyData(TextUtils.join(",", toFetch));

        // Perform request
        performRequest(responseCall);
    }

    /**
     * Perform request and handle callback
     * @param responseCall Call to perform
     */
    private void performRequest(Call<Map<String, Response>> responseCall){
        responseCall.enqueue(new Callback<Map<String, Response>>() {
            @Override
            public void onResponse(Call<Map<String, Response>> call, retrofit2.Response<Map<String, Response>> response) {
                // Success
                if (response.code() == 200) {
                    handleSuccessResponse(response);
                } else {
                    // If failure, return the server error (or the error for returning that)
                    try{ listener.onError(response.errorBody().string()); }
                    catch (Exception e) { listener.onError(e.getMessage()); }
                }
            }

            private void handleSuccessResponse(retrofit2.Response<Map<String, Response>> response) {
                ArrayList<CurrencyPrice> prices = new ArrayList<>();

                for (Map.Entry<String, Response> entry: response.body().entrySet()) {
                    prices.add(
                            new CurrencyPrice(entry.getKey(),
                                    entry.getValue().priceUsd,
                                    entry.getValue().priceEur,
                                    entry.getValue().priceBtc)
                    );
                }

                listener.onPricesUpdated(prices);
            }

            @Override
            public void onFailure(Call<Map<String, Response>> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    /**
     * Interface for returning data to the repository
     */
    public interface PricesResponseListener {
        void onPricesUpdated(ArrayList<CurrencyPrice> prices);
        void onError(String message);
    }

    /**
     * Retrofit request interfaces
     */
    private interface CryptoCompareAPI {
        @Headers("Content-Type: application/json")
        @GET("pricemulti?tsyms=EUR,BTC,USD")
        Call<Map<String, Response>> getCurrencyData(
                @Query("fsyms") String currenciesToFetch
        );
    }

    /**
     * POJO used for converting the JSON response to Java
     */
    class Response {
        @SerializedName("EUR")
        @Expose
        private Float priceEur;
        @SerializedName("BTC")
        @Expose
        private Float priceBtc;
        @SerializedName("USD")
        @Expose
        private Float priceUsd;
    }
}
