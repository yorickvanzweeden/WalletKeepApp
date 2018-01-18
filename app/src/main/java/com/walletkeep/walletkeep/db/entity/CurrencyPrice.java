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

    @ColumnInfo(name = "price_eur")
    @android.arch.persistence.room.TypeConverters({TypeConverters.class})
    private BigDecimal priceEur;

    @ColumnInfo(name = "price_usd")
    @android.arch.persistence.room.TypeConverters({TypeConverters.class})
    private BigDecimal priceUsd;

    @ColumnInfo(name = "price_btc")
    @android.arch.persistence.room.TypeConverters({TypeConverters.class})
    private BigDecimal priceBtc;

    @ColumnInfo(name = "change24hEur")
    private float change24hEur;

    @ColumnInfo(name = "change24hUsd")
    private float change24hUsd;

    @ColumnInfo(name = "change24hBtc")
    private float change24hBtc;

    @ColumnInfo(name = "last_updated")
    @android.arch.persistence.room.TypeConverters({TypeConverters.class})
    private Date lastUpdated;

    public CurrencyPrice(String currencyTicker) {
        this.currencyTicker = currencyTicker;
    }

    @Ignore
    public CurrencyPrice(String currencyTicker, BigDecimal priceEur, BigDecimal priceUsd, BigDecimal priceBtc,
                         float change24hEur, float change24hUsd, float change24hBtc) {
        this.currencyTicker = currencyTicker;
        this.priceUsd = priceUsd;
        this.priceEur = priceEur;
        this.priceBtc = priceBtc;
        this.change24hEur = change24hEur;
        this.change24hUsd = change24hUsd;
        this.change24hBtc = change24hBtc;
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

    public float getChange24hEur() { return change24hEur; }
    public void setChange24hEur(float change24hEur) { this.change24hEur = change24hEur; }

    public float getChange24hUsd() { return change24hUsd; }
    public void setChange24hUsd(float change24hUsd) { this.change24hUsd = change24hUsd; }

    public float getChange24hBtc() { return change24hBtc; }
    public void setChange24hBtc(float change24hBtc) { this.change24hBtc = change24hBtc; }
}
