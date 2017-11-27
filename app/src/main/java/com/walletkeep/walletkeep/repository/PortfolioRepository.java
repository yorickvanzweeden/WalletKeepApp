package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Portfolio;

import java.util.ArrayList;
import java.util.List;

public class PortfolioRepository {
    // Repository instance
    private static PortfolioRepository sInstance;

    // Database instance
    private final AppDatabase mDatabase;

    /**
     * Constructor: Initializes repository with database
     * @param database Database to use
     */
    public PortfolioRepository(AppDatabase database) {
        mDatabase = database;
    }

    /**
     * Gets instance of the repository (singleton)
     * @param database Database to use
     * @return Instance of the repository
     */
    public static PortfolioRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (PortfolioRepository.class) {
                if (sInstance == null) {
                    sInstance = new PortfolioRepository(database);
                }
            }
        }
        return sInstance;
    }


    public LiveData<Portfolio> getPortfolio(int portfolioId) {
        return mDatabase.portfolioDao().getById(portfolioId);
    }

    public LiveData<List<Portfolio>> getPortfolios() {
        return mDatabase.portfolioDao().getAll();
    }

    public void addPortfolio(Portfolio portfolio) {
        AsyncTask.execute(() -> mDatabase.portfolioDao().insert(portfolio));
    }

    public void addPortfolios(List<Portfolio> portfolios) {
        AsyncTask.execute(() -> mDatabase.portfolioDao().insertAll(portfolios));
    }
}