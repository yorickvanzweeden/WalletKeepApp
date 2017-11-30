package com.walletkeep.walletkeep.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

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

    @Query("SELECT asset.id, asset.wallet_id, asset.currency_ticker, asset.amount " +
            "FROM asset JOIN wallet ON asset.wallet_id = wallet.id " +
            "WHERE wallet.portfolio_id LIKE :portfolioId")
    public abstract LiveData<List<Asset>> getByPortfolioId(int portfolioId);
}