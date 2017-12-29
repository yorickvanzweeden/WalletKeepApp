package com.walletkeep.walletkeep.db.entity;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.walletkeep.walletkeep.db.DateConverter;

import java.util.Date;

@Entity(indices = {@Index("currency_ticker")},
        foreignKeys = {
                @ForeignKey(
                        entity = Currency.class,
                        parentColumns = "ticker",
                        childColumns = "currency_ticker",
                        onDelete = ForeignKey.CASCADE
                )
        })
public class CurrencyPrice {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "currency_ticker")
    private String currencyTicker;

    @ColumnInfo(name = "price_usd")
    private float priceUsd;

    @ColumnInfo(name = "price_eur")
    private float priceEur;

    @ColumnInfo(name = "price_btc")
    private float priceBtc;

    @ColumnInfo(name = "change1h")
    private float change1h;

    @ColumnInfo(name = "change24h")
    private float change24h;

    @ColumnInfo(name = "change7d")
    private float change7d;

    @ColumnInfo(name = "last_updated")
    @TypeConverters({DateConverter.class})
    private Date lastUpdated;

    public CurrencyPrice(String currencyTicker) {
        this.currencyTicker = currencyTicker;
    }

    @Ignore
    public CurrencyPrice(String currencyTicker, float priceUsd, float priceEur, float priceBtc,
                         float change1h, float change24h, float change7d, Date lastUpdated) {
        this.currencyTicker = currencyTicker;
        this.priceUsd = priceUsd;
        this.priceEur = priceEur;
        this.priceBtc = priceBtc;
        this.change1h = change1h;
        this.change24h = change24h;
        this.change7d = change7d;
        this.lastUpdated = lastUpdated;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrencyTicker() { return currencyTicker; }

    public void setCurrencyTicker(String currencyTicker) { this.currencyTicker = currencyTicker; }

    public float getPriceUsd() { return priceUsd; }

    public void setPriceUsd(float priceUsd) { this.priceUsd = priceUsd; }

    public float getPriceEur() { return priceEur; }

    public void setPriceEur(float priceEur) { this.priceEur = priceEur; }

    public float getPriceBtc() { return priceBtc; }

    public void setPriceBtc(float priceBtc) { this.priceBtc = priceBtc; }

    public Date getLastUpdated() { return lastUpdated; }

    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }

    public float getChange1h() { return change1h; }

    public void setChange1h(float change1h) { this.change1h = change1h; }

    public float getChange24h() { return change24h; }

    public void setChange24h(float change24h) { this.change24h = change24h; }

    public float getChange7d() { return change7d; }

    public void setChange7d(float change7d) { this.change7d = change7d; }
}
