package com.walletkeep.walletkeep.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.walletkeep.walletkeep.db.entity.Portfolio;
import com.walletkeep.walletkeep.db.entity.Wallet;

import java.util.List;

@Dao
public interface WalletDao {
    @Query("SELECT * FROM wallet")
    LiveData<List<Wallet>> getAll();

    @Query("SELECT * FROM wallet WHERE id LIKE :id LIMIT 1")
    LiveData<Portfolio> getById(int id);

    @Insert
    void insertAll(List<Wallet> wallets);

    @Update
    void update(Wallet wallet);

    @Delete
    void delete(Wallet wallet);
}