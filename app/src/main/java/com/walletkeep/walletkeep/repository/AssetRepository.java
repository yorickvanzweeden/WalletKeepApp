package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.Portfolio;

import java.util.List;

public class AssetRepository {
    // Repository instance
    private static AssetRepository sInstance;

    // Database instance
    private final AppDatabase mDatabase;

    /**
     * Constructor: Initializes repository with database
     * @param database Database to use
     */
    public AssetRepository(AppDatabase database) {
        mDatabase = database;
    }

    /**
     * Gets instance of the repository (singleton)
     * @param database Database to use
     * @return Instance of the repository
     */
    public static AssetRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (AssetRepository.class) {
                if (sInstance == null) {
                    sInstance = new AssetRepository(database);
                }
            }
        }
        return sInstance;
    }

    public LiveData<List<Asset>> getAggregatedAssets(int portfolioId) {
        return mDatabase.assetDao().getAggregatedAssets(portfolioId);
    }

}