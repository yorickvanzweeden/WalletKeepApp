package com.walletkeep.walletkeep.db.entity;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class AggregatedAsset {
    @Embedded
    public Asset asset;

    @Relation(parentColumn = "currency_ticker", entityColumn = "currency_ticker", entity = CurrencyPrice.class)
    public List<CurrencyPrice> prices;


    public String getTicker(){
        return asset.getCurrencyTicker();
    }

    public float getAmount(){
        return asset.getAmount();
    }

    public CurrencyPrice getLatestCurrencyPrice(){
        if(prices == null || prices.size() == 0) return null;

        return prices.get(0);
    }
}
