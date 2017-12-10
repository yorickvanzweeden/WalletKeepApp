package com.walletkeep.walletkeep.db.entity;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class AggregatedAsset {
    @Embedded
    public Asset asset;

    @Relation(parentColumn = "currency_ticker", entityColumn = "currency_ticker", entity = CurrencyPrice.class)
    public List<CurrencyPrice> prices;


    /**
     * Gets ticker of the currency
     * @return Ticker of the currency
     */
    public String getTicker(){
        return asset.getCurrencyTicker();
    }

    /**
     * Gets the amount owned of the currency
     * @return The amount owned of the currency
     */
    public float getAmount(){
        return asset.getAmount();
    }

    /**
     * Gets latest price of the currency
     * @return Latest price of the currency
     */
    public CurrencyPrice getLatestCurrencyPrice(){
        if(prices == null || prices.size() == 0) return null;
        //TODO: Does this return the lastest or the first?
        return prices.get(0);
    }
}
