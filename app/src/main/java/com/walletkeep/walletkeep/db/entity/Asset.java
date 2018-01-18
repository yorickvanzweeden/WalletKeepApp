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

@Entity(indices = {@Index("wallet_id"), @Index("currency_ticker")},
        foreignKeys = {
        @ForeignKey(
                entity = Wallet.class,
                parentColumns = "id",
                childColumns = "wallet_id",
                onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = Currency.class,
                parentColumns = "ticker",
                childColumns = "currency_ticker",
                onDelete = ForeignKey.CASCADE
        )
})
public class Asset {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "wallet_id")
    private int walletId;

    @ColumnInfo(name = "currency_ticker")
    private String currencyTicker;

    @ColumnInfo(name = "amount")
    @android.arch.persistence.room.TypeConverters({TypeConverters.class})
    private BigDecimal amount;

    @ColumnInfo(name = "timestamp")
    @android.arch.persistence.room.TypeConverters({TypeConverters.class})
    private Date timestamp;

    // Constructors
    public Asset(int walletId, String currencyTicker, BigDecimal amount){
        this.walletId = walletId;
        this.currencyTicker = currencyTicker;
        this.amount = amount;
    }

    // Getters and setters
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getWalletId() { return walletId; }

    public void setWalletId(int walletId) { this.walletId = walletId; }

    public String getCurrencyTicker() { return currencyTicker; }

    public void setCurrencyTicker(String currencyTicker) { this.currencyTicker = currencyTicker; }

    public BigDecimal getAmount() { return amount; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Date getTimestamp() { return timestamp; }

    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    /**
     * Checks for equality on assets for amount and currency(!)
     * @param aThat Other asset
     * @return True if the assets match
     */
    @Ignore
    @Override
    public boolean equals(Object aThat) {
        // check for self-comparison
        if ( this == aThat ) return true;

        // check if same type
        if ( !(aThat instanceof Asset) ) return false;

        //cast to native object is now safe
        Asset that = (Asset)aThat;

        //now a proper field-by-field evaluation can be made
        return this.amount.equals(that.amount) &&
                this.currencyTicker.equals(that.currencyTicker);
    }
}