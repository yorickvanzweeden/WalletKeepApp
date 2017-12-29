package com.walletkeep.walletkeep.db.entity;

public class AggregatedAsset {
    public String currencyTicker;
    public float amount;
    public float price_eur;
    public float change1h;
    public float change24h;
    public float change7d;

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


    /**
     * Gets percentage of change of the last hour
     * @return Latest percentage of change of the last hour
     */
    public float getChange(String s){
        if (s == null) return change24h;
        switch (s.trim().toUpperCase()) {
            case "1H":
                return change1h;
            case "7D":
                return change7d;
            default:
                return change24h;
        }
    }

}
