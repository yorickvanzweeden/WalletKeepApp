package com.walletkeep.walletkeep.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.Portfolio;

import java.util.List;

@Dao
public abstract class AssetDao implements BaseDao<Asset> {
    @Query("SELECT * FROM asset")
    public abstract LiveData<List<Asset>> getAll();

    @Query("SELECT * FROM asset WHERE id LIKE :id LIMIT 1")
    public abstract LiveData<Asset> getById(int id);

    @Query("SELECT * FROM asset WHERE wallet_id LIKE :walletId")
    public abstract LiveData<List<Asset>> getByWalletId(int walletId);

    @Query("SELECT asset.currency_ticker, SUM(asset.amount) FROM portfolio " +
            "JOIN wallet ON portfolio.id = wallet.portfolio_id " +
            "JOIN asset ON wallet.id = asset.wallet_id " +
            "WHERE portfolio.id LIKE :portfolioId " +
            "GROUP BY asset.currency_ticker")
    public abstract LiveData<List<Asset>> getAggregatedAssets(int portfolioId);
}