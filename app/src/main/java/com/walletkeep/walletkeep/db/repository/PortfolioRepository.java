package com.walletkeep.walletkeep.db.repository;

import android.arch.lifecycle.LiveData;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Portfolio;

import java.util.List;

public class PortfolioRepository {
    private static PortfolioRepository sInstance;
    private final AppDatabase mDatabase;

    public PortfolioRepository(AppDatabase database) {
        mDatabase = database;
    }

    public LiveData<List<Portfolio>> loadPortfolios() {
        LiveData<List<Portfolio>> watch = mDatabase.portfolioDao().getAll();
        return watch;
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