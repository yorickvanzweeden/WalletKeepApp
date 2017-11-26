package com.walletkeep.walletkeep.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.walletkeep.walletkeep.db.entity.Coin;

import java.util.List;

@Dao
public interface CoinDao {
    @Query("SELECT * FROM coin")
    LiveData<List<Coin>> getAll();

    @Query("SELECT * FROM coin WHERE id LIKE :id LIMIT 1")
    LiveData<Coin> getById(int id);

    @Query("SELECT * FROM coin WHERE wallet_id LIKE :walletId")
    LiveData<List<Coin>> getByWalletId(int walletId);

    @Query("SELECT coin.id, coin.wallet_id, coin.currency_ticker, coin.amount " +
            "FROM coin JOIN wallet ON coin.wallet_id = wallet.id " +
            "WHERE wallet.portfolio_id LIKE :portfolioId")
    LiveData<List<Coin>> getByPortfolioId(int portfolioId);

    @Insert
    void insertAll(List<Coin> coins);

    @Update
    void update(Coin coin);

    @Delete
    void delete(Coin coin);
}