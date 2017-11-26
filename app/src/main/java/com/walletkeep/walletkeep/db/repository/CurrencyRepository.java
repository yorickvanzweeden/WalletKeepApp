package com.walletkeep.walletkeep.db.repository;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Currency;

import java.util.ArrayList;
import java.util.List;

public class CurrencyRepository {
    // Repository instance
    private static CurrencyRepository sInstance;

    // Database instance
    private final AppDatabase mDatabase;

    /**
     * Constructor: Initializes repository with database
     * @param database Database to use
     */
    public CurrencyRepository(AppDatabase database) {
        mDatabase = database;
    }

    /**
     * Gets instance of the repository (singleton)
     * @param database Database to use
     * @return Instance of the repository
     */
    public static CurrencyRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (CurrencyRepository.class) {
                if (sInstance == null) {
                    sInstance = new CurrencyRepository(database);
                }
            }
        }
        return sInstance;
    }



    public LiveData<Currency> getCurrency(String ticker) {
        return mDatabase.currencyDao().getByTicker(ticker);
    }

    public LiveData<List<Currency>> getCurrencies() {
        return mDatabase.currencyDao().getAll();
    }

    public void addCurrency(Currency currency) {
        List<Currency> currencyList = new ArrayList<>();
        currencyList.add(currency);
        AsyncTask.execute(() -> mDatabase.currencyDao().insertAll(currencyList));
    }

    public void addCurrencies(List<Currency> currencies) {
        AsyncTask.execute(() -> mDatabase.currencyDao().insertAll(currencies));
    }
}