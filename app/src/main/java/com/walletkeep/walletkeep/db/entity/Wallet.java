package com.walletkeep.walletkeep.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Relation;

import java.util.List;

@Entity(indices = {@Index("portfolio_id"), @Index("exchange_id")},
        foreignKeys = {
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
    private String exchangeId;

    // Constructors for ExchangeWallet and NakedWallet
    public Wallet(int portfolioId){
        this.portfolioId = portfolioId;
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

    public String getExchangeId() { return exchangeId; }

    public void setExchangeId(String exchangeId) { this.exchangeId = exchangeId; }
}