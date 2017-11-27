package com.walletkeep.walletkeep.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.walletkeep.walletkeep.db.entity.Coin;

import java.util.List;

@Dao
public abstract class CoinDao implements BaseDao<Coin> {
    @Query("SELECT * FROM coin")
    public abstract LiveData<List<Coin>> getAll();

    @Query("SELECT * FROM coin WHERE id LIKE :id LIMIT 1")
    public abstract LiveData<Coin> getById(int id);

    @Query("SELECT * FROM coin WHERE wallet_id LIKE :walletId")
    public abstract LiveData<List<Coin>> getByWalletId(int walletId);

    @Query("SELECT coin.id, coin.wallet_id, coin.currency_ticker, coin.amount " +
            "FROM coin JOIN wallet ON coin.wallet_id = wallet.id " +
            "WHERE wallet.portfolio_id LIKE :portfolioId")
    public abstract LiveData<List<Coin>> getByPortfolioId(int portfolioId);
}