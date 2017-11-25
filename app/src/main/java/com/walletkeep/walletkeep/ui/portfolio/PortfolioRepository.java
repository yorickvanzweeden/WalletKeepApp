package com.walletkeep.walletkeep.ui.portfolio;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Portfolio;

import java.util.List;

public class PortfolioRepository {
    private static PortfolioRepository sInstance;
    private final AppDatabase mDatabase;
    private MediatorLiveData<List<Portfolio>> mObservableProducts;

    public PortfolioRepository(AppDatabase database) {
        mDatabase = database;
        mObservableProducts = new MediatorLiveData<>();

        mObservableProducts.addSource(mDatabase.portfolioDao().loadPortfolios(),
                exchangeCredentialsList -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null) {
                        mObservableProducts.postValue(exchangeCredentialsList);
                    }
                });
    }

    public LiveData<List<Portfolio>> loadPortfolios() {
        LiveData<List<Portfolio>> watch = mDatabase.portfolioDao().loadPortfolios();
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