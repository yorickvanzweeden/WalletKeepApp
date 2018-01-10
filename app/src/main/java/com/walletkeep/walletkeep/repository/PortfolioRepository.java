package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;

import com.walletkeep.walletkeep.AppExecutors;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Portfolio;

import java.util.List;

public class PortfolioRepository {
    // Database instance
    private final AppDatabase database;
    private final AppExecutors executors;


    /**
     * Constructor: Initializes repository with database
     * @param database Database to use
     */
    public PortfolioRepository(AppDatabase database, AppExecutors executors) {
        this.database = database;
        this.executors = executors;
    }

    /**
     * Gets a list of portfolios from the database
     * @return List of portfolios
     */
    public LiveData<List<Portfolio>> getPortfolios() { return database.portfolioDao().getAll(); }

    /**
     * Adds a portfolio (async)
     * @param portfolio Portfolio to insert in the database
     */
    public void addPortfolio(Portfolio portfolio) {
        executors.diskIO().execute(() -> database.portfolioDao().insert(portfolio));
    }

    /**
     * Delete a portfolio (async)
     * @param portfolio Portfolio to delete
     */
    public void deletePortfolio(Portfolio portfolio) {
        executors.diskIO().execute(() -> database.portfolioDao().delete(portfolio));
    }
}