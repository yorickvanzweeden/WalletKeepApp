package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.walletkeep.walletkeep.AppExecutors;
import com.walletkeep.walletkeep.api.data.FirebaseService;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Currency;
import com.walletkeep.walletkeep.db.entity.CurrencyPrice;

import java.util.Date;
import java.util.List;

public class CurrencyRepository {
    // Database instance
    private final AppDatabase database;
    private final AppExecutors executors;
    private final FirebaseService firebaseService;

    /**
     * Constructor: Initializes repository with database
     * @param database Database to use
     */
    public CurrencyRepository(AppDatabase database, AppExecutors executors) {
        this.database = database;
        this.executors = executors;
        this.firebaseService = new FirebaseService(getUpdateListener());
    }

    /**
     * Gets a list of portfolios from the database
     * @return List of portfolios
     */
    public LiveData<List<String>> getCurrencies() { return database.assetDao().getActiveCurrencies(); }

    public void updateFirebaseWatchList(List<String> currencies) {
        firebaseService.updateWatchList(currencies);
    }

    private FirebaseService.UpdateListener getUpdateListener() {
        return new FirebaseService.UpdateListener() {
            @Override
            public void insertPrice(CurrencyPrice price) {
                executors.diskIO().execute(() -> {
                    database.currencyPriceDao().deleteByTicker(price.getCurrencyTicker());
                    price.setLastUpdated(new Date());
                    try { database.currencyPriceDao().insert(price); }
                    catch (Exception e) {
                        database.currencyDao().insert(new Currency(price.getCurrencyTicker(), price.getCurrencyTicker()));
                        database.currencyPriceDao().insert(price);
                        Log.w("###", "Not found, but inserted: " + price.getCurrencyTicker());
                    }
                });
            }

            @Override
            public void insertCurrencies(List<Currency> currencies) {
                executors.diskIO().execute(() -> {
                    database.currencyDao().insertAll(currencies);
                });
            }

            @Override
            public void onError(DatabaseError error) {
                //TODO: Fix errors to main thread
            }
        };
    }
}