package com.walletkeep.walletkeep.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.walletkeep.walletkeep.db.entity.Wallet;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

import java.util.List;

@Dao
public interface WalletDao {
    @Query("SELECT * FROM wallet WHERE portfolio_id LIKE :portfolioId")
    LiveData<List<WalletWithRelations>> getAll(int portfolioId);

    @Query("SELECT * FROM wallet WHERE id LIKE :id LIMIT 1")
    LiveData<Wallet> getById(int id);

    @Insert
    void insertAll(List<Wallet> wallets);

    @Update
    void update(Wallet wallet);

    @Delete
    void delete(Wallet wallet);
}