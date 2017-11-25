package com.walletkeep.walletkeep.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Currency {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "ticker")
    private String ticker;

    // Constructor
    public Currency(int id, String name, String ticker){
        this.id = id;
        this.name = name;
        this.ticker = ticker;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getTicker() { return ticker; }

    public void setTicker(String ticker) { this.ticker = ticker; }
}