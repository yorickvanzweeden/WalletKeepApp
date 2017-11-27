package com.walletkeep.walletkeep.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;

import java.util.List;

@Dao
public abstract class ExchangeCredentialsDao implements BaseDao<ExchangeCredentials> {
    @Query("SELECT * FROM exchangecredentials")
    public abstract LiveData<List<ExchangeCredentials>> getAll();

    @Query("SELECT * FROM exchangecredentials WHERE id LIKE :id LIMIT 1")
    public abstract LiveData<ExchangeCredentials> getById(int id);

    @Query("SELECT * FROM exchangecredentials WHERE wallet_id LIKE :walletId LIMIT 1")
    public abstract LiveData<ExchangeCredentials> getByWalletId(int walletId);
}