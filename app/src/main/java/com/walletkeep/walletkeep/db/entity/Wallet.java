package com.walletkeep.walletkeep.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Portfolio.class,
                parentColumns = "id",
                childColumns = "portfolio_id"
        ),
        @ForeignKey(
                entity = Exchange.class,
                parentColumns = "id",
                childColumns = "exchange_id"
        )
})
public class Wallet {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "portfolio_id")
    private int portfolioId;

    @ColumnInfo(name = "exchange_id")
    private int exchangeId;

    // Constructors for ExchangeWallet and NakedWallet
    public Wallet(int id, int portfolioId, int exchangeId){
        this.id = id;
        this.portfolioId = portfolioId;
        this.exchangeId = exchangeId;
    }

    public Wallet(int id, int portfolioId, String address){
        this.id = id;
        this.portfolioId = portfolioId;
        this.address = address;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public int getPortfolioId() { return portfolioId; }

    public void setPortfolioId(int portfolioId) { this.portfolioId = portfolioId; }

    public int getExchangeId() { return exchangeId; }

    public void setExchangeId(int exchangeId) { this.exchangeId = exchangeId; }
}