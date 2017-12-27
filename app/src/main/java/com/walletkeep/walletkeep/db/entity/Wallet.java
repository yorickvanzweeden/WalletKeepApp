package com.walletkeep.walletkeep.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index("portfolio_id"), @Index("exchange_name"), @Index("address_currency")},
        foreignKeys = {
        @ForeignKey(
                entity = Portfolio.class,
                parentColumns = "id",
                childColumns = "portfolio_id",
                onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = Exchange.class,
                parentColumns = "name",
                childColumns = "exchange_name",
                onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = Currency.class,
                parentColumns = "ticker",
                childColumns = "address_currency",
                onDelete = ForeignKey.CASCADE
        )
})
public class Wallet {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "address_currency")
    private String addressCurrency;

    @ColumnInfo(name = "portfolio_id")
    private int portfolioId;

    @ColumnInfo(name = "exchange_name")
    private String exchangeName;

    // Constructors for ExchangeWallet and NakedWallet
    public Wallet(int portfolioId){
        this.portfolioId = portfolioId;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getAddressCurrency() { return addressCurrency; }

    public void setAddressCurrency(String addressCurrency) { this.addressCurrency = addressCurrency; }

    public int getPortfolioId() { return portfolioId; }

    public void setPortfolioId(int portfolioId) { this.portfolioId = portfolioId; }

    public String getExchangeName() { return exchangeName; }

    public void setExchangeName(String exchangeName) { this.exchangeName = exchangeName; }
}