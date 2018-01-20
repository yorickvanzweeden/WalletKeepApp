package com.walletkeep.walletkeep.api.data;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.entity.CurrencyPrice;
import com.walletkeep.walletkeep.util.SynchronisedTicket;

import java.math.BigDecimal;
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
    private SynchronisedTicket ticket;

    public CryptoCompareService(PricesResponseListener listener) { this.listener = listener; }

    /**
     * Fetch currency data from CryptoCompare
     */
    public void fetch(List<String> toFetch, boolean delete) {
        // Create request
        CryptoCompareAPI api = RetrofitClient.getClient(baseUrl).create(CryptoCompareAPI.class);

        String s = TextUtils.join(",", toFetch);
        if (s.length() >= 300) {
            List<String> strings = partitionString(s);
            ticket = new SynchronisedTicket<CurrencyPrice>(strings.size());
            for (String subString: strings) {
                // Perform request
                performRequest(api.getCurrencyData(subString), delete);
            }
        } else {
            // Perform request
            ticket = new SynchronisedTicket(1);
            performRequest(api.getCurrencyData(s), delete);
        }
    }

    private List<String> partitionString(String s) {
        List<String> stringList = new ArrayList<>();
        if (s.length() > 300) {
            int lastIndex = s.lastIndexOf(',', 300);
            stringList.addAll(partitionString(s.substring(0, lastIndex)));
            stringList.addAll(partitionString(s.substring(lastIndex + 1, s.length())));
        } else {
            stringList.add(s);
        }
        return stringList;
    }

    /**
     * Perform request and handle callback
     * @param responseCall Call to perform
     */
    private void performRequest(Call<Response> responseCall, boolean delete){
        responseCall.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                // Success
                if (response.code() == 200) {
                    ticket.add(response.body().getPrices());
                    insertPricesOnLastResponse();
                } else {
                    insertPricesOnLastResponse();
                    // If failure, return the server error (or the error for returning that)
                    try{ listener.onError(response.errorBody().string()); }
                    catch (Exception e) { listener.onError(e.getMessage()); }
                }
            }

            private void insertPricesOnLastResponse(){
                if (ticket.isLast()) listener.onPricesUpdated((List<CurrencyPrice>)ticket.get(), delete);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                insertPricesOnLastResponse();

                try {
                    String s = call.request().url().queryParameter("fsyms");
                    listener.onError(String.format("%1$s %2$s could not be fetched", (s.indexOf(',') < 0 ? "Currency" : "Currencies"), s));
                }
                catch (Exception e) {
                    listener.onError("Unknown error occurred during fetching prices");
                }
            }
        });
    }

    /**
     * Interface for returning data to the repository
     */
    public interface PricesResponseListener {
        void onPricesUpdated(List<CurrencyPrice> prices, boolean delete);
        void onError(String message);
    }

    /**
     * Retrofit request interfaces
     */
    private interface CryptoCompareAPI {
        @Headers("Content-Type: application/json")
        @GET("pricemultifull?tsyms=EUR,BTC,USD")
        Call<Response> getCurrencyData(
                @Query("fsyms") String currenciesToFetch
        );
    }

    /**
     * POJO used for converting the JSON response to Java
     */
    class Response {
        @SerializedName("RAW")
        @Expose
        private Map<String, ArrayMap<String, ToCurrency>> response;

        @SerializedName("EUR")
        @Expose
        private String priceEur;
        @SerializedName("BTC")
        @Expose
        private String priceBtc;
        @SerializedName("USD")
        @Expose
        private String priceUsd;

        List<CurrencyPrice> getPrices(){
            List<CurrencyPrice> prices = new ArrayList<>();
            if (response == null) return prices;
            for (Map.Entry<String, ArrayMap<String, ToCurrency>> entry: response.entrySet()) {
                prices.add(new CurrencyPrice(entry.getKey(),
                        new BigDecimal(entry.getValue().get("EUR").price),
                        new BigDecimal(entry.getValue().get("USD").price),
                        new BigDecimal(entry.getValue().get("BTC").price),
                        entry.getValue().get("EUR").change24h,
                        entry.getValue().get("USD").change24h,
                        entry.getValue().get("BTC").change24h));
            }
            return prices;
        }
    }

    private class ToCurrency {
        @SerializedName("PRICE")
        @Expose
        private String price;
        @SerializedName("CHANGEPCT24HOUR")
        @Expose
        private Float change24h;
    }
}
