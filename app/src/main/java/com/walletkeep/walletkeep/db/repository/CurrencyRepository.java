package com.walletkeep.walletkeep.db.repository;

import android.arch.lifecycle.LiveData;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Currency;

import java.util.ArrayList;
import java.util.List;

public class CurrencyRepository {
    private static CurrencyRepository sInstance;
    private final AppDatabase mDatabase;

    public CurrencyRepository(AppDatabase database) {
        mDatabase = database;
    }

    public LiveData<Currency> getCurrency(int currencyId) {
        return mDatabase.currencyDao().getById(currencyId);
    }

    public LiveData<List<Currency>> getCurrencies() {
        return mDatabase.currencyDao().getAll();
    }

    public void addCurrency(Currency currency) {
        List<Currency> currencyList = new ArrayList<>();
        currencyList.add(currency);
        mDatabase.currencyDao().insertAll(currencyList);
    }

    public void addCurrencies(List<Currency> currencies) {
        mDatabase.currencyDao().insertAll(currencies);
    }


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
}