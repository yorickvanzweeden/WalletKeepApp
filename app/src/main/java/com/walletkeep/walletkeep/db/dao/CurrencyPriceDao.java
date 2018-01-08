package com.walletkeep.walletkeep.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.walletkeep.walletkeep.db.entity.CurrencyPrice;

@Dao
public abstract class CurrencyPriceDao implements BaseDao<CurrencyPrice>{
    @Query("SELECT * FROM currencyprice WHERE currency_ticker LIKE :ticker ORDER BY last_updated DESC LIMIT 1")
    public abstract LiveData<CurrencyPrice> getByTicker(String ticker);

    @Query("DELETE FROM currencyprice")
    public abstract void deleteAll();
}
