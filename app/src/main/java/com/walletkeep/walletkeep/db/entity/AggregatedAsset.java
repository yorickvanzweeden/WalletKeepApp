package com.walletkeep.walletkeep.db.entity;

public class AggregatedAsset {
    public String currencyTicker;
    public float amount;
    public float price_eur;

    /**
     * Gets ticker of the currency
     * @return Ticker of the currency
     */
    public String getTicker(){
        return currencyTicker;
    }

    /**
     * Gets the amount owned of the currency
     * @return The amount owned of the currency
     */
    public float getAmount(){
        return amount;
    }

    /**
     * Gets latest price_eur of the currency
     * @return Latest price_eur of the currency
     */
    public float getLatestCurrencyPrice(){
        return price_eur;
    }
}
