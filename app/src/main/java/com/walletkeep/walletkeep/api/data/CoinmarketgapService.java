package com.walletkeep.walletkeep.api.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.walletkeep.walletkeep.api.CurrencyTickerCorrection;
import com.walletkeep.walletkeep.api.RetrofitClient;
import com.walletkeep.walletkeep.db.DateConverter;
import com.walletkeep.walletkeep.db.entity.Currency;
import com.walletkeep.walletkeep.db.entity.CurrencyPrice;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public class CoinmarketgapService {
    PricesResponseListener listener;

    public CoinmarketgapService(PricesResponseListener listener) {
        this.listener = listener;
    }

    /**
     * Fetch currency data from Coinmarketgap
     */
    public void fetch() {
        // Create request
        CoinmarketgapApi api = RetrofitClient.getClient("https://api.coinmarketcap.com/v1/")
                .create(CoinmarketgapApi.class);
        Call<List<CoinmarketgapResponse>> responseCall = api.getCurrencyData();

        // Perform request
        performRequest(responseCall);
    }

    /**
     * Perform request and handle callback
     * @param responseCall Call to perform
     */
    private void performRequest(Call responseCall){
        responseCall.enqueue(new Callback<List<CoinmarketgapResponse>>() {
            @Override
            public void onResponse(Call<List<CoinmarketgapResponse>> call,
                                   Response<List<CoinmarketgapResponse>> response) {
                // Success
                if (response.code() == 200) {
                    handleSuccessResponse(response.body());
                } else {
                    // If failure, return the server error (or the error for returning that)
                    try{ listener.onError(response.errorBody().string()); }
                    catch (Exception e) { listener.onError(e.getMessage()); }
                }
            }

            public void handleSuccessResponse(List<CoinmarketgapResponse> responses) {
                ArrayList<CurrencyPrice> prices = new ArrayList<>();
                ArrayList<Currency> currencies = new ArrayList<>();

                for(CoinmarketgapResponse response: responses) {
                    prices.add(response.getCurrencyPrice());
                    currencies.add(response.getCurrency());
                }

                listener.onCurrenciesUpdated(currencies);
                listener.onPricesUpdated(prices);
            }

            @Override
            public void onFailure(Call<List<CoinmarketgapResponse>> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    /**
     * Interface for returning data to the repository
     */
    public interface PricesResponseListener {
        void onCurrenciesUpdated(ArrayList<Currency> currencies);
        void onPricesUpdated(ArrayList<CurrencyPrice> prices);
        void onError(String message);
    }

    /**
     * Retrofit request interfaces
     */
    private interface CoinmarketgapApi {
        @Headers("Content-Type: application/json")
        @GET("ticker/?convert=EUR&limit=1200")
        Call<List<CoinmarketgapResponse>> getCurrencyData();
    }

    /**
     * POJO used for converting the JSON response to Java
     */
    private class CoinmarketgapResponse {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("symbol")
        @Expose
        private String symbol;
        @SerializedName("rank")
        @Expose
        private String rank;
        @SerializedName("price_usd")
        @Expose
        private String priceUsd;
        @SerializedName("price_btc")
        @Expose
        private String priceBtc;
        @SerializedName("24h_volume_usd")
        @Expose
        private String _24hVolumeUsd;
        @SerializedName("market_cap_usd")
        @Expose
        private String marketCapUsd;
        @SerializedName("available_supply")
        @Expose
        private String availableSupply;
        @SerializedName("total_supply")
        @Expose
        private String totalSupply;
        @SerializedName("max_supply")
        @Expose
        private Object maxSupply;
        @SerializedName("percent_change_1h")
        @Expose
        private String percentChange1h;
        @SerializedName("percent_change_24h")
        @Expose
        private String percentChange24h;
        @SerializedName("percent_change_7d")
        @Expose
        private String percentChange7d;
        @SerializedName("last_updated")
        @Expose
        private String lastUpdated;
        @SerializedName("price_eur")
        @Expose
        private String priceEur;
        @SerializedName("24h_volume_eur")
        @Expose
        private String _24hVolumeEur;
        @SerializedName("market_cap_eur")
        @Expose
        private String marketCapEur;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public String getPriceUsd() {
            return priceUsd;
        }

        public void setPriceUsd(String priceUsd) {
            this.priceUsd = priceUsd;
        }

        public String getPriceBtc() {
            return priceBtc;
        }

        public void setPriceBtc(String priceBtc) {
            this.priceBtc = priceBtc;
        }

        public String get24hVolumeUsd() {
            return _24hVolumeUsd;
        }

        public void set24hVolumeUsd(String _24hVolumeUsd) {
            this._24hVolumeUsd = _24hVolumeUsd;
        }

        public String getMarketCapUsd() {
            return marketCapUsd;
        }

        public void setMarketCapUsd(String marketCapUsd) {
            this.marketCapUsd = marketCapUsd;
        }

        public String getAvailableSupply() {
            return availableSupply;
        }

        public void setAvailableSupply(String availableSupply) {
            this.availableSupply = availableSupply;
        }

        public String getTotalSupply() {
            return totalSupply;
        }

        public void setTotalSupply(String totalSupply) {
            this.totalSupply = totalSupply;
        }

        public Object getMaxSupply() {
            return maxSupply;
        }

        public void setMaxSupply(Object maxSupply) {
            this.maxSupply = maxSupply;
        }

        public String getPercentChange1h() {
            return percentChange1h;
        }

        public void setPercentChange1h(String percentChange1h) {
            this.percentChange1h = percentChange1h;
        }

        public String getPercentChange24h() {
            return percentChange24h;
        }

        public void setPercentChange24h(String percentChange24h) {
            this.percentChange24h = percentChange24h;
        }

        public String getPercentChange7d() {
            return percentChange7d;
        }

        public void setPercentChange7d(String percentChange7d) {
            this.percentChange7d = percentChange7d;
        }

        public String getLastUpdated() {
            return lastUpdated;
        }

        public void setLastUpdated(String lastUpdated) {
            this.lastUpdated = lastUpdated;
        }

        public String getPriceEur() {
            return priceEur;
        }

        public void setPriceEur(String priceEur) {
            this.priceEur = priceEur;
        }

        public String get24hVolumeEur() {
            return _24hVolumeEur;
        }

        public void set24hVolumeEur(String _24hVolumeEur) {
            this._24hVolumeEur = _24hVolumeEur;
        }

        public String getMarketCapEur() {
            return marketCapEur;
        }

        public void setMarketCapEur(String marketCapEur) {
            this.marketCapEur = marketCapEur;
        }

        /**
         * Gets currency price in USD, EUR and BTC
         * @return currency price
         */
        public CurrencyPrice getCurrencyPrice() {
            if (percentChange24h == null) percentChange24h = "0";
            if (percentChange1h == null) percentChange1h = "0";
            if (percentChange7d == null) percentChange7d = "0";

            return new CurrencyPrice(
                    CurrencyTickerCorrection.correct(symbol),
                    Float.parseFloat(priceUsd),
                    Float.parseFloat(priceEur),
                    Float.parseFloat(priceBtc),
                    Float.parseFloat(percentChange1h),
                    Float.parseFloat(percentChange24h),
                    Float.parseFloat(percentChange7d),
                    DateConverter.fromTimestamp(Long.parseLong(lastUpdated))
            );
        }

        /**
         * Gets currency
         * @return currency
         */
        public Currency getCurrency() {
            return new Currency(name, CurrencyTickerCorrection.correct(symbol));
        }
    }
}
