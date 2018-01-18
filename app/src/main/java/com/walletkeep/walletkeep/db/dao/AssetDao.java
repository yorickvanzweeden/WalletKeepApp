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

    @Query("SELECT assets.currency_ticker AS currencyTicker," +
            "  assets.amount AS amount," +
            "  prices.price_eur AS price_eur, " +
            "  prices.price_usd AS price_usd, " +
            "  prices.price_btc AS price_btc, " +
            "  prices.change1h AS change1h, " +
            "  prices.change24h AS change24h, " +
            "  prices.change7d AS change7d, " +
            "  prices.last_updated AS priceTimeStamp " +
            "FROM " +
            "  ( SELECT asset.currency_ticker, SUM(asset.amount) AS amount " +
            "    FROM asset JOIN wallet ON asset.wallet_id = wallet.id " +
            "    JOIN portfolio ON wallet.portfolio_id = portfolio.id " +
            "    WHERE portfolio_id = :portfolioId " +
            "    GROUP BY asset.currency_ticker ) AS assets" +
            "  LEFT JOIN " +
            "  (SELECT c1.* FROM currencyprice c1 LEFT JOIN currencyprice c2" +
            "    ON (c1.currency_ticker = c2.currency_ticker AND c1.last_updated < c2.last_updated)" +
            "  WHERE c2.last_updated IS NULL) AS prices" +
            "  ON assets.currency_ticker = prices.currency_ticker " +
            "WHERE assets.amount != 0 " +
            "GROUP BY assets.currency_ticker")
    public abstract LiveData<List<AggregatedAsset>> getAggregatedAssets(int portfolioId);
}