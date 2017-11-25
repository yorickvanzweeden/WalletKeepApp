package com.walletkeep.walletkeep.db.repository;

import android.arch.lifecycle.LiveData;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Exchange;

import java.util.ArrayList;
import java.util.List;

public class ExchangeRepository {
    private static ExchangeRepository sInstance;
    private final AppDatabase mDatabase;

    public ExchangeRepository(AppDatabase database) {
        mDatabase = database;
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
        mDatabase.exchangeDao().insertAll(exchangeList);
    }

    public void addExchanges(List<Exchange> exchanges) {
        mDatabase.exchangeDao().insertAll(exchanges);
    }


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
}