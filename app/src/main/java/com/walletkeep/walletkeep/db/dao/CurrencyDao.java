package com.walletkeep.walletkeep.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.walletkeep.walletkeep.db.entity.Currency;

import java.util.List;

@Dao
public interface CurrencyDao {
    @Query("SELECT * FROM currency")
    LiveData<List<Currency>> getAll();

    @Query("SELECT * FROM currency WHERE id LIKE :id LIMIT 1")
    LiveData<Currency> getById(int id);

    @Query("SELECT * FROM currency WHERE ticker LIKE :ticker LIMIT 1")
    LiveData<Currency> getByTicker(int ticker);

    @Insert
    void insertAll(List<Currency> currencies);

    @Update
    void update(Currency currency);

    @Delete
    void delete(Currency currency);
}
