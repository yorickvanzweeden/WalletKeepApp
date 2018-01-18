package com.walletkeep.walletkeep.db.entity;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.walletkeep.walletkeep.db.TypeConverters;

import java.math.BigDecimal;
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
    @android.arch.persistence.room.TypeConverters({TypeConverters.class})
    private BigDecimal priceUsd;

    @ColumnInfo(name = "price_eur")
    @android.arch.persistence.room.TypeConverters({TypeConverters.class})
    private BigDecimal priceEur;

    @ColumnInfo(name = "price_btc")
    @android.arch.persistence.room.TypeConverters({TypeConverters.class})
    private BigDecimal priceBtc;

    @ColumnInfo(name = "change1h")
    private float change1h;

    @ColumnInfo(name = "change24h")
    private float change24h;

    @ColumnInfo(name = "change7d")
    private float change7d;

    @ColumnInfo(name = "last_updated")
    @android.arch.persistence.room.TypeConverters({TypeConverters.class})
    private Date lastUpdated;

    public CurrencyPrice(String currencyTicker) {
        this.currencyTicker = currencyTicker;
    }

    @Ignore
    public CurrencyPrice(String currencyTicker, BigDecimal priceUsd, BigDecimal priceEur, BigDecimal priceBtc,
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

    @Ignore
    public CurrencyPrice(String currencyTicker, BigDecimal priceUsd, BigDecimal priceEur, BigDecimal priceBtc) {
        this.currencyTicker = currencyTicker;
        this.priceUsd = priceUsd;
        this.priceEur = priceEur;
        this.priceBtc = priceBtc;
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

    public BigDecimal getPriceUsd() { return priceUsd; }

    public void setPriceUsd(BigDecimal priceUsd) { this.priceUsd = priceUsd; }

    public BigDecimal getPriceEur() { return priceEur; }

    public void setPriceEur(BigDecimal priceEur) { this.priceEur = priceEur; }

    public BigDecimal getPriceBtc() { return priceBtc; }

    public void setPriceBtc(BigDecimal priceBtc) { this.priceBtc = priceBtc; }

    public Date getLastUpdated() { return lastUpdated; }

    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }

    public float getChange1h() { return change1h; }

    public void setChange1h(float change1h) { this.change1h = change1h; }

    public float getChange24h() { return change24h; }

    public void setChange24h(float change24h) { this.change24h = change24h; }

    public float getChange7d() { return change7d; }

    public void setChange7d(float change7d) { this.change7d = change7d; }
}
