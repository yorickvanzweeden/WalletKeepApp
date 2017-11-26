package com.walletkeep.walletkeep;

import android.app.Application;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.repository.PortfolioRepository;
import com.walletkeep.walletkeep.db.repository.WalletRepository;

public class WalletKeepApp extends Application {

    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppExecutors = new AppExecutors();
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this, mAppExecutors);
    }

    public PortfolioRepository getPortfolioRepository() {
        return PortfolioRepository.getInstance(getDatabase());
    }
    public WalletRepository getWalletRepository() {
        return WalletRepository.getInstance(getDatabase());
    }
}
