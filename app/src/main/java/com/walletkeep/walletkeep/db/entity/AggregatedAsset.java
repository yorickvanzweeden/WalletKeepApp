package com.walletkeep.walletkeep.db.entity;

import com.walletkeep.walletkeep.db.TypeConverters;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;

//Per portfolio
public class AggregatedAsset {
    public String currencyTicker;

    @android.arch.persistence.room.TypeConverters({TypeConverters.class})
    public BigDecimal amount;

    @android.arch.persistence.room.TypeConverters({TypeConverters.class})
    public BigDecimal price_eur;
    @android.arch.persistence.room.TypeConverters({TypeConverters.class})
    public BigDecimal price_usd;
    @android.arch.persistence.room.TypeConverters({TypeConverters.class})
    public BigDecimal price_btc;

    public float change24hEur;
    public float change24hUsd;
    public float change24hBtc;
    @android.arch.persistence.room.TypeConverters({TypeConverters.class})
    public Date priceTimeStamp;

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
    public BigDecimal getAmount(){
        return amount;
    }

    /**
     * Gets latest price_eur of the currency
     * @return Latest price_eur of the currency
     */
    public BigDecimal getPriceEur(){ return price_eur == null ? BigDecimal.ZERO : price_eur; }

    /**
     * Gets latest price_eur of the currency
     * @return Latest price_eur of the currency
     */
    public BigDecimal getPriceUsd(){ return price_usd == null ? BigDecimal.ZERO : price_usd; }

    /**
     * Gets latest price_eur of the currency
     * @return Latest price_eur of the currency
     */
    public BigDecimal getPriceBtc(){ return price_btc == null ? BigDecimal.ZERO : price_btc; }

    /**
     * Gets the value of the asset (amount*price)
     * @return Latest value of the asset
     */
    public BigDecimal getValueEur(){
        return price_eur == null ? BigDecimal.ZERO : price_eur.multiply(amount);
    }

    /**
     * Gets the value of the asset (amount*price)
     * @return Latest value of the asset
     */
    public BigDecimal getValueUsd(){
        return price_usd == null ? BigDecimal.ZERO : price_usd.multiply(amount);

    }

    /**
     * Gets the value of the asset (amount*price)
     * @return Latest value of the asset
     */
    public BigDecimal getValueBtc(){
        return price_btc == null ? BigDecimal.ZERO : price_btc.multiply(amount);

    }


    public Date getPriceTimeStamp(){ return priceTimeStamp; }
    /**
     * Gets percentage of change of the last hour
     * @return Latest percentage of change of the last hour
     */
    public float getChange(String s){
        if (s == null) return change24hEur;
        switch (s.trim().toUpperCase()) {
            case "USD":
                return change24hUsd;
            case "BTC":
                return change24hBtc;
            default:
                return change24hEur;
        }
    }

    public BigDecimal getPrice(String s){
        if (s == null) return getPriceEur();
        switch (s.trim().toUpperCase()) {
            case "USD":
                return getPriceUsd();
            case "BTC":
                return getPriceBtc();
            default:
                return getPriceEur();
        }
    }
    public BigDecimal getValue(String s){
        if (s == null) return getValueEur();
        switch (s.trim().toUpperCase()) {
            case "USD":
                return getValueUsd();
            case "BTC":
                return getValueBtc();
            default:
                return getValueEur();
        }
    }

    public static class AssetComparator implements Comparator<AggregatedAsset> {
        @Override
        public int compare(AggregatedAsset o1, AggregatedAsset o2) {
            return o2.getValueEur().compareTo(o1.getValueEur());
        }
    }
}
