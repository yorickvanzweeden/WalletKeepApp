package com.walletkeep.walletkeep.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.walletkeep.walletkeep.db.entity.Currency;

import java.util.List;

@Dao
public abstract class CurrencyDao implements BaseDao<Currency>{
    @Query("SELECT * FROM currency")
    public abstract LiveData<List<Currency>> getAll();

    @Query("SELECT * FROM currency WHERE ticker LIKE :ticker LIMIT 1")
    public abstract LiveData<Currency> getByTicker(String ticker);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insert(Currency currency);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertAll(List<Currency> currency);
}
