package com.walletkeep.walletkeep.db.repository;

import android.arch.lifecycle.LiveData;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;

import java.util.ArrayList;
import java.util.List;

public class ExchangeCredentialsRepository {
    private static ExchangeCredentialsRepository sInstance;
    private final AppDatabase mDatabase;

    public ExchangeCredentialsRepository(AppDatabase database) {
        mDatabase = database;
    }

    public LiveData<ExchangeCredentials> getExchangeCredentials(int exchangeCredentialsId) {
        return mDatabase.exchangeCredentialsDao().getById(exchangeCredentialsId);
    }

    public LiveData<List<ExchangeCredentials>> getExchangeCredentials() {
        return mDatabase.exchangeCredentialsDao().getAll();
    }

    public void addExchangeCredentials(ExchangeCredentials exchangeCredentials) {
        List<ExchangeCredentials> exchangeCredentialsList = new ArrayList<>();
        exchangeCredentialsList.add(exchangeCredentials);
        mDatabase.exchangeCredentialsDao().insertAll(exchangeCredentialsList);
    }

    public void addExchangeCredentialss(List<ExchangeCredentials> exchangeCredentials) {
        mDatabase.exchangeCredentialsDao().insertAll(exchangeCredentials);
    }


    public static ExchangeCredentialsRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (ExchangeCredentialsRepository.class) {
                if (sInstance == null) {
                    sInstance = new ExchangeCredentialsRepository(database);
                }
            }
        }
        return sInstance;
    }
}