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

    @Query("SELECT assets.currency_ticker AS currencyTicker," +
            "  assets.amount AS amount," +
            "  prices.price_eur AS price_eur " +
            "FROM " +
            "  ( SELECT a1.currency_ticker, SUM(a1.amount) AS amount" +
            "    FROM asset a1 LEFT JOIN asset a2" +
            "      ON (a1.currency_ticker = a2.currency_ticker AND" +
            "          a1.wallet_id = a2.wallet_id AND a1.timestamp < a2.timestamp)" +
            "    WHERE a2.timestamp IS NULL AND" +
            "      a1.wallet_id IN (SELECT id FROM wallet WHERE portfolio_id = :portfolioId)" +
            "      GROUP BY a1.currency_ticker" +
            "  ) AS assets" +
            "  LEFT JOIN " +
            "  (SELECT c1.* FROM currencyprice c1 LEFT JOIN currencyprice c2" +
            "    ON (c1.currency_ticker = c2.currency_ticker AND c1.last_updated < c2.last_updated)" +
            "  WHERE c2.last_updated IS NULL) AS prices" +
            "  ON assets.currency_ticker = prices.currency_ticker " +
            "GROUP BY assets.currency_ticker")
    public abstract LiveData<List<AggregatedAsset>> getAggregatedAssets(int portfolioId);
}