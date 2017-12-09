package com.walletkeep.walletkeep.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Exchange {
    @PrimaryKey @NonNull
    @ColumnInfo(name = "name")
    private String name;

    // Constructor
    public Exchange(String name){
        this.name = name;
    }

    // Getters and setters
    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public enum Exchanges {
        BINANCE("Binance"),
        GDAX("GDAX");

        private final String text;

        Exchanges(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}