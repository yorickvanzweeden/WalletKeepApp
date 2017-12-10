package com.walletkeep.walletkeep.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.walletkeep.walletkeep.db.entity.AggregatedAsset;
import com.walletkeep.walletkeep.db.entity.Asset;

import java.util.List;

@Dao
public abstract class AssetDao implements BaseDao<Asset> {
    @Query("SELECT * FROM asset")
    public abstract LiveData<List<Asset>> getAll();

    @Query("SELECT * FROM asset WHERE id LIKE :id LIMIT 1")
    public abstract LiveData<Asset> getById(int id);

    @Query("SELECT * FROM asset WHERE wallet_id LIKE :walletId")
    public abstract LiveData<List<Asset>> getByWalletId(int walletId);

    @Query("SELECT * FROM portfolio " +
            "JOIN wallet ON portfolio.id = wallet.portfolio_id " +
            "JOIN asset ON wallet.id = asset.wallet_id " +
            "JOIN currencyprice ON asset.currency_ticker = currencyprice.currency_ticker " +
            "WHERE portfolio.id LIKE :portfolioId " +
            "GROUP BY asset.currency_ticker " +
            "ORDER BY currencyprice.last_updated")
    public abstract LiveData<List<AggregatedAsset>> getAggregatedAssets(int portfolioId);
}