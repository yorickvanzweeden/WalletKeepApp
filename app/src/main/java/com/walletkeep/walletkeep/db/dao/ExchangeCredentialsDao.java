package com.walletkeep.walletkeep.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;

import java.util.List;

@Dao
public interface ExchangeCredentialsDao {
    @Query("SELECT * FROM exchangecredentials")
    LiveData<List<ExchangeCredentials>> getAll();

    @Query("SELECT * FROM exchangecredentials WHERE id LIKE :id LIMIT 1")
    LiveData<ExchangeCredentials> getById(int id);

    @Query("SELECT * FROM exchangecredentials WHERE wallet_id LIKE :walletId LIMIT 1")
    LiveData<ExchangeCredentials> getByWalletId(int walletId);

    @Insert
    void insertAll(List<ExchangeCredentials> exchangeCredentials);

    @Update
    void update(ExchangeCredentials exchangeCredentials);

    @Delete
    void delete(ExchangeCredentials exchangeCredentials);
}