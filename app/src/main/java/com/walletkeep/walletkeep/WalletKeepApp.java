package com.walletkeep.walletkeep;

import android.app.Application;
import android.content.Context;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.repository.AssetRepository;
import com.walletkeep.walletkeep.repository.PortfolioRepository;
import com.walletkeep.walletkeep.repository.WalletRepository;

public class WalletKeepApp extends Application {

    private AppExecutors mAppExecutors;
    private static Context mContext;

    public static Context getContext(){
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppExecutors = new AppExecutors();
        mContext = this;
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
    public AssetRepository getAssetRepository() {
        return AssetRepository.getInstance(getDatabase());
    }
}
