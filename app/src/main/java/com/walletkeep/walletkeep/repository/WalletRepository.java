package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Asset;
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

    public LiveData<List<Asset>> getPortfolioAssets(int portfolioId){
        return mDatabase.assetDao().getByPortfolioId(portfolioId);
    }

    public void addWallet(Wallet wallet) {
        AsyncTask.execute(() -> mDatabase.walletDao().insert(wallet));
    }

    public void fetchWalletData(WalletWithRelations wallet){
        // Observe callback and save to db if needed
        ApiService.AssetResponseListener listener = new ApiService.AssetResponseListener() {
            @Override
            public void onAssetsUpdated(ArrayList<Asset> assets) {
                for (Asset asset : wallet.assets) {
                    AsyncTask.execute(() -> mDatabase.assetDao().delete(asset));
                }
                for (Asset asset : assets) {
                    AsyncTask.execute(() -> mDatabase.assetDao().insert(asset));
                }
            }

            @Override
            public void onError(String message) {
                //TODO: Do something with message
            }
        };

        // Create ApiService
        ApiService.Factory apiServiceFactory = new ApiService.Factory(wallet, listener);
        ApiService apiService = apiServiceFactory.create();

        // Fetch data
        apiService.fetch();
    }
}