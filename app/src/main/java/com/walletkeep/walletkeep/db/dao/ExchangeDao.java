package com.walletkeep.walletkeep.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.walletkeep.walletkeep.db.entity.Exchange;

import java.util.List;

@Dao
public abstract class ExchangeDao implements BaseDao<Exchange> {
    @Query("SELECT * FROM exchange")
    public abstract LiveData<List<Exchange>> getAll();

    @Query("SELECT * FROM exchange WHERE name LIKE :name LIMIT 1")
    public abstract LiveData<Exchange> getByName(String name);
}
