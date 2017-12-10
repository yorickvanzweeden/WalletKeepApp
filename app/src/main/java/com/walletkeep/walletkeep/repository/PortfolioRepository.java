package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Portfolio;

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

    /**
     * Gets a list of portfolios from the database
     * @return List of portfolios
     */
    public LiveData<List<Portfolio>> getPortfolios() {
        return mDatabase.portfolioDao().getAll();
    }

    /**
     * Adds a portfolio (async)
     * @param portfolio Portfolio to insert in the database
     */
    public void addPortfolio(Portfolio portfolio) {
        AsyncTask.execute(() -> mDatabase.portfolioDao().insert(portfolio));
    }
}