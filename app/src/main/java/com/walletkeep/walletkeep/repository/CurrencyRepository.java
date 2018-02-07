package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;

import com.walletkeep.walletkeep.AppExecutors;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Currency;

public class CurrencyRepository {
    // Database instance
    private final AppDatabase database;
    private final AppExecutors executors;

    /**
     * Constructor: Initializes repository with database
     * @param database Database to use
     */
    public CurrencyRepository(AppDatabase database, AppExecutors executors) {
        this.database = database;
        this.executors = executors;
    }

    public LiveData<Currency> getCurrency(String currencyTicker) {
        return database.currencyDao().getByTicker(currencyTicker);
    }
}