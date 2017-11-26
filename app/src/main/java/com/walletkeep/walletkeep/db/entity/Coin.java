package com.walletkeep.walletkeep.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index("wallet_id"), @Index("currency_ticker")},
        foreignKeys = {
        @ForeignKey(
                entity = Wallet.class,
                parentColumns = "id",
                childColumns = "wallet_id"
        ),
        @ForeignKey(
                entity = Currency.class,
                parentColumns = "ticker",
                childColumns = "currency_ticker"
        )
})
public class Coin {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "wallet_id")
    private int walletId;

    @ColumnInfo(name = "currency_ticker")
    private String currencyTicker;

    @ColumnInfo(name = "amount")
    private float amount;

    // Constructors
    public Coin(int walletId, String currencyTicker, float amount){
        this.walletId = walletId;
        this.currencyTicker = currencyTicker;
        this.amount = amount;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public int getWalletId() { return walletId; }

    public void setWalletId(int walletId) { this.walletId = walletId; }

    public String getCurrencyTicker() { return currencyTicker; }

    public void setCurrencyTicker(String currencyTicker) { this.currencyTicker = currencyTicker; }

    public float getAmount() { return amount; }

    public void setAmount(float amount) { this.amount = amount; }
}