package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Coin;
import com.walletkeep.walletkeep.db.entity.Wallet;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

import java.util.ArrayList;
import java.util.List;

public class WalletRepository {
    // Repository instance
    private static WalletRepository sInstance;

    // Database instance
    private final AppDatabase mDatabase;

    /**
     * Constructor: Initializes repository with database
     * @param database Database to use
     */
    public WalletRepository(AppDatabase database) {
        this.mDatabase = database;
    }

    /**
     * Gets instance of the repository (singleton)
     * @param database Database to use
     * @return Instance of the repository
     */
    public static WalletRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (WalletRepository.class) {
                if (sInstance == null) {
                    sInstance = new WalletRepository(database);
                }
            }
        }
        return sInstance;
    }


    public LiveData<List<WalletWithRelations>> getWallets(int portfolioId) {
        return mDatabase.walletDao().getAll(portfolioId);
    }

    public LiveData<List<Coin>> getPortfolioCoins(int portfolioId){
        return mDatabase.coinDao().getByPortfolioId(portfolioId);
    }

    public void addWallet(Wallet wallet) {
        AsyncTask.execute(() -> mDatabase.walletDao().insert(wallet));
    }

    public void fetchWalletData(WalletWithRelations wallet){
        // Observe callback and save to db if needed
        ApiService.CoinResponseListener listener = coins -> {
                for(Coin coin:wallet.coins) {
                    AsyncTask.execute(() -> mDatabase.coinDao().delete(coin));
                }
                for(Coin coin:coins) {
                    AsyncTask.execute(() -> mDatabase.coinDao().insert(coin));
                }
        };

        // Create ApiService
        ApiService.Factory apiServiceFactory = new ApiService.Factory(wallet, listener);
        ApiService apiService = apiServiceFactory.create();

        // Fetch data
        apiService.fetch(wallet.getCredentials());
    }
}