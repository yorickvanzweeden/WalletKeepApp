package com.walletkeep.walletkeep.api.data;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walletkeep.walletkeep.api.CurrencyTickerCorrection;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.DateConverter;
import com.walletkeep.walletkeep.db.entity.CurrencyPrice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public class CryptocompareService {
    private PricesResponseListener listener;

    public CryptocompareService(PricesResponseListener listener) {
        this.listener = listener;
    }

    /**
     * Fetch currency data from Coinmarketcap
     */
    public void fetch(List<String> toFetch) {
        // Create request
        CryptocompareAPI api = RetrofitClient.getClient("https://min-api.cryptocompare.com/data/")
                .create(CryptocompareAPI.class);

        Call<List<CryptocompareResponse>> responseCall = api.getCurrencyData(TextUtils.join(",", toFetch));

        // Perform request
        performRequest(responseCall);
    }

    /**
     * Perform request and handle callback
     * @param responseCall Call to perform
     */
    private void performRequest(Call responseCall){
        responseCall.enqueue(new Callback<List<CryptocompareResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<CryptocompareResponse>> call,
                                   @NonNull Response<List<CryptocompareResponse>> response) {
                // Success
                if (response.code() == 200) {
                    handleSuccessResponse(response.body());
                } else {
                    // If failure, return the server error (or the error for returning that)
                    try{ listener.onError(response.errorBody().string()); }
                    catch (Exception e) { listener.onError(e.getMessage()); }
                }
            }

            private void handleSuccessResponse(List<CryptocompareResponse> responses) {
                ArrayList<CurrencyPrice> prices = new ArrayList<>();

                for(CryptocompareResponse response: responses) {
                   prices.add(response.getCurrencyPrice());
                }

                listener.onPricesUpdated(prices);
            }

            @Override
            public void onFailure(@NonNull Call<List<CryptocompareResponse>> call,
                                  @NonNull Throwable t) {

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
    private interface CryptocompareAPI {
        @Headers("Content-Type: application/json")
        @GET("pricemulti?fsyms={pair}&tsyms=BTC,USD,EUR")
        Call<List<CryptocompareResponse>> getCurrencyData(@Path("pair") String toFetch);
    }

    /**
     * POJO used for converting the JSON response to Java
     */
    private class CryptocompareResponse {

        @SerializedName("name")
        @Expose
        private String symbol;
        @SerializedName("symbol")
        @Expose
        private PriceInformation priceInformation;

        /**
         * Gets currency price in USD, EUR and BTC
         * @return currency price
         */
        private CurrencyPrice getCurrencyPrice() {
            return new CurrencyPrice(
                    CurrencyTickerCorrection.correct(symbol),
                    Float.parseFloat(priceInformation.usd),
                    Float.parseFloat(priceInformation.eur),
                    Float.parseFloat(priceInformation.btc),
                    Float.parseFloat("0"),
                    Float.parseFloat("0"),
                    Float.parseFloat("0"),
                    DateConverter.fromTimestamp(Long.parseLong("0"))
            );
        }
    }

    private class PriceInformation{
        @SerializedName("BTC")
        @Expose
        private String btc;
        @SerializedName("USD")
        @Expose
        private String usd;
        @SerializedName("EUR")
        @Expose
        private String eur;

        public BigDecimal BTC = new BigDecimal(btc);
        public BigDecimal USD = new BigDecimal(usd);
        public BigDecimal EUR = new BigDecimal(eur);

    }

}
