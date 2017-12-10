package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.walletkeep.walletkeep.api.data.CoinmarketgapService;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.AggregatedAsset;
import com.walletkeep.walletkeep.db.entity.CurrencyPrice;
import com.walletkeep.walletkeep.util.RateLimiter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AssetRepository {
    // Repository instance
    private static AssetRepository sInstance;

    // Database instance
    private final AppDatabase mDatabase;

    // Rate limiter prevent too many requests
    private RateLimiter<String> apiRateLimit = new RateLimiter<>(3, TimeUnit.MINUTES);


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

    public LiveData<List<AggregatedAsset>> getAggregatedAssets(int portfolioId) {
        return mDatabase.assetDao().getAggregatedAssets(portfolioId);
    }

    public void fetchCurrencyPrices(){
        // Don't execute API calls if rate limit is applied
        if (!apiRateLimit.shouldFetch(Integer.toString(1))) { return; }

        // Observe callback and save to db if needed
        CoinmarketgapService.PricesResponseListener listener = new CoinmarketgapService.PricesResponseListener() {

            @Override
            public void onPricesUpdated(ArrayList<CurrencyPrice> prices) {
                AsyncTask.execute(() -> mDatabase.currencyPriceDao().insertAll(prices));
            }

            @Override
            public void onError(String message) {
                //TODO: Do something with message
            }
        };

        // Create ApiService
        CoinmarketgapService service = new CoinmarketgapService(listener);

        // Fetch data
        service.fetch();
    }
}