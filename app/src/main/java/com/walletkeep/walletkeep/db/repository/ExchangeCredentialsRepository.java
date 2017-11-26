package com.walletkeep.walletkeep.db.repository;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;

import java.util.ArrayList;
import java.util.List;

public class ExchangeCredentialsRepository {
    // Repository instance
    private static ExchangeCredentialsRepository sInstance;

    // Database instance
    private final AppDatabase mDatabase;

    /**
     * Constructor: Initializes repository with database
     * @param database Database to use
     */
    public ExchangeCredentialsRepository(AppDatabase database) {
        mDatabase = database;
    }

    /**
     * Gets instance of the repository (singleton)
     * @param database Database to use
     * @return Instance of the repository
     */
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



    public LiveData<ExchangeCredentials> getExchangeCredentials(int exchangeCredentialsId) {
        return mDatabase.exchangeCredentialsDao().getById(exchangeCredentialsId);
    }

    public LiveData<List<ExchangeCredentials>> getExchangeCredentials() {
        return mDatabase.exchangeCredentialsDao().getAll();
    }

    public void addExchangeCredentials(ExchangeCredentials exchangeCredentials) {
        List<ExchangeCredentials> exchangeCredentialsList = new ArrayList<>();
        exchangeCredentialsList.add(exchangeCredentials);
        AsyncTask.execute(() -> mDatabase.exchangeCredentialsDao().insertAll(exchangeCredentialsList));
    }

    public void addExchangeCredentialss(List<ExchangeCredentials> exchangeCredentials) {
        AsyncTask.execute(() -> mDatabase.exchangeCredentialsDao().insertAll(exchangeCredentials));
    }


}