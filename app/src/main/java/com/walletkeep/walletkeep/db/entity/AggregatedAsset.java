package com.walletkeep.walletkeep.db.entity;


import android.arch.persistence.room.ColumnInfo;

public class AggregatedAsset {
    @ColumnInfo(name = "currency_ticker")
    public String currencyTicker;
    public float amount;
}
