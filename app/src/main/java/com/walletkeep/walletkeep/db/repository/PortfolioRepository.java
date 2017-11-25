package com.walletkeep.walletkeep.db.repository;

import android.arch.lifecycle.LiveData;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Portfolio;

import java.util.ArrayList;
import java.util.List;

public class PortfolioRepository {
    private static PortfolioRepository sInstance;
    private final AppDatabase mDatabase;

    public PortfolioRepository(AppDatabase database) {
        mDatabase = database;
    }

    public LiveData<Portfolio> getPortfolio(int portfolioId) {
        return mDatabase.portfolioDao().getById(portfolioId);
    }

    public LiveData<List<Portfolio>> getPortfolios() {
        return mDatabase.portfolioDao().getAll();
    }

    public void addPortfolio(Portfolio portfolio) {
        List<Portfolio> portfolioList = new ArrayList<>();
        portfolioList.add(portfolio);
        mDatabase.portfolioDao().insertAll(portfolioList);
    }

    public void addPortfolios(List<Portfolio> portfolios) {
        mDatabase.portfolioDao().insertAll(portfolios);
    }


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
}