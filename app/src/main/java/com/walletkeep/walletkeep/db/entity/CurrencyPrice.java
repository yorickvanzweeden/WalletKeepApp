package com.walletkeep.walletkeep.db.entity;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.walletkeep.walletkeep.db.DateConverter;

import java.util.Date;

@Entity(indices = {@Index("currency_ticker"), @Index("exchange_name")},
        foreignKeys = {
                @ForeignKey(
                        entity = Currency.class,
                        parentColumns = "ticker",
                        childColumns = "currency_ticker",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Exchange.class,
                        parentColumns = "name",
                        childColumns = "exchange_name",
                        onDelete = ForeignKey.CASCADE
                )
        })
public class CurrencyPrice {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "currency_ticker")
    private String currencyTicker;

    @ColumnInfo(name = "exchange_name")
    private String exchangeName;

    @ColumnInfo(name = "timestamp")
    @TypeConverters({DateConverter.class})
    private Date timestamp;

    @ColumnInfo(name = "price")
    private float price;

    public CurrencyPrice(String currencyTicker, String exchangeName, Date timestamp, float price) {
        this.currencyTicker = currencyTicker;
        this.exchangeName = exchangeName;
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

    public String getExchangeName() { return exchangeName; }

    public void setExchangeName(String exchangeName) { this.exchangeName = exchangeName; }

    public Date getTimestamp() { return timestamp; }

    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public float getPrice() { return price; }

    public void setPrice(float price) { this.price = price; }
}
