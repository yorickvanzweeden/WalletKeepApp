package com.walletkeep.walletkeep.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Wallet.class,
                parentColumns = "id",
                childColumns = "wallet_id"
        ),
        @ForeignKey(
                entity = Currency.class,
                parentColumns = "id",
                childColumns = "currency_id"
        )
})
public class Coin {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "wallet_id")
    private int walletId;

    @ColumnInfo(name = "currency_id")
    private int currencyId;

    @ColumnInfo(name = "amount")
    private float amount;

    // Constructors
    public Coin(int id, int walletId, int currencyId){
        this.id = id;
        this.walletId = walletId;
        this.currencyId = currencyId;
        this.amount = 0;
    }

    public Coin(int id, int walletId, int currencyId, float amount){
        this.id = id;
        this.walletId = walletId;
        this.currencyId = currencyId;
        this.amount = amount;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public int getWalletId() { return walletId; }

    public void setWalletId(int walletId) { this.walletId = walletId; }

    public int getCurrencyId() { return currencyId; }

    public void setCurrencyId(int currencyId) { this.currencyId = currencyId; }

    public float getAmount() { return amount; }

    public void setAmount(float amount) { this.amount = amount; }
}