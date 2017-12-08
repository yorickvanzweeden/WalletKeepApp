package com.walletkeep.walletkeep.db.entity;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.walletkeep.walletkeep.db.DateConverter;

import java.util.Date;

@Entity(indices = {@Index("currency_ticker"), @Index("exchange_id")},
        foreignKeys = {
                @ForeignKey(
                        entity = Currency.class,
                        parentColumns = "ticker",
                        childColumns = "currency_ticker",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Exchange.class,
                        parentColumns = "id",
                        childColumns = "exchange_id",
                        onDelete = ForeignKey.CASCADE
                )
        })
public class CurrencyPrice {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "currency_ticker")
    private String currencyTicker;

    @ColumnInfo(name = "exchange_id")
    private String exchangeId;

    @ColumnInfo(name = "timestamp")
    @TypeConverters({DateConverter.class})
    private Date timestamp;

    @ColumnInfo(name = "price")
    private float price;

    public CurrencyPrice(String currencyTicker, String exchangeId, Date timestamp, float price) {
        this.currencyTicker = currencyTicker;
        this.exchangeId = exchangeId;
        this.timestamp = timestamp;
        this.price = price;
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

    public String getExchangeId() { return exchangeId; }

    public void setExchangeId(String exchangeId) { this.exchangeId = exchangeId; }

    public Date getTimestamp() { return timestamp; }

    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public float getPrice() { return price; }

    public void setPrice(float price) { this.price = price; }
}
