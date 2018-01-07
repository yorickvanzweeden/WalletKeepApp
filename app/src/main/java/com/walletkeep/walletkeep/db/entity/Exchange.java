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
    public Exchange(@NonNull String name){
        this.name = name;
    }

    // Getters and setters
    public String getName() { return name; }

    public void setName(@NonNull String name) {
        this.name = name;
    }
}