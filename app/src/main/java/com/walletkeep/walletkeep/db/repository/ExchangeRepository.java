package com.walletkeep.walletkeep.db.repository;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Exchange;

import java.util.ArrayList;
import java.util.List;

public class ExchangeRepository {
    // Repository instance
    private static ExchangeRepository sInstance;

    // Database instance
    private final AppDatabase mDatabase;

    /**
     * Constructor: Initializes repository with database
     * @param database Database to use
     */
    public ExchangeRepository(AppDatabase database) {
        mDatabase = database;
    }

    /**
     * Gets instance of the repository (singleton)
     * @param database Database to use
     * @return Instance of the repository
     */
    public static ExchangeRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (ExchangeRepository.class) {
                if (sInstance == null) {
                    sInstance = new ExchangeRepository(database);
                }
            }
        }
        return sInstance;
    }



    public LiveData<Exchange> getExchange(int exchangeId) {
        return mDatabase.exchangeDao().getById(exchangeId);
    }

    public LiveData<List<Exchange>> getExchanges() {
        return mDatabase.exchangeDao().getAll();
    }

    public void addExchange(Exchange exchange) {
        List<Exchange> exchangeList = new ArrayList<>();
        exchangeList.add(exchange);
        AsyncTask.execute(() -> mDatabase.exchangeDao().insertAll(exchangeList));
    }

    public void addExchanges(List<Exchange> exchanges) {
        AsyncTask.execute(() -> mDatabase.exchangeDao().insertAll(exchanges));
    }


}