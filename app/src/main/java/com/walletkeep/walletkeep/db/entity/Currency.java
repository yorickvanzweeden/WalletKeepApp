package com.walletkeep.walletkeep.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class Currency {
    @SerializedName("ticker") @Expose
    @PrimaryKey @NonNull
    @ColumnInfo(name = "ticker")
    private String ticker;

    @SerializedName("name") @Expose
    @ColumnInfo(name = "name")
    private String name;

    // Constructor
    public Currency(String name,@NonNull String ticker){
        this.name = name;
        this.ticker = ticker;
    }

    // Getters and setters
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getTicker() { return ticker; }

    public void setTicker(@NonNull String ticker) { this.ticker = ticker; }
}