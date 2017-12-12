package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.data.CoinmarketgapService;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.AggregatedAsset;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.Currency;
import com.walletkeep.walletkeep.db.entity.CurrencyPrice;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
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
    private RateLimiter<String> priceApiRateLimit = new RateLimiter<>(3, TimeUnit.MINUTES);
    private RateLimiter<String> apiRateLimit = new RateLimiter<>(10, TimeUnit.SECONDS);


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

    /**
     * Gets a list of aggregated assets of a portfolio
     * @param portfolioId Id of the portfolio
     * @return List of aggregated assets
     */
    public LiveData<List<AggregatedAsset>> getAggregatedAssets(int portfolioId) {
        return mDatabase.assetDao().getAggregatedAssets(portfolioId);
    }

    /**
     * Gets a list of aggregated assets of a portfolio
     * @param portfolioId Id of the portfolio
     * @return List of aggregated assets
     */
    public LiveData<List<WalletWithRelations>> getWallets(int portfolioId) {
        return mDatabase.walletDao().getAll(portfolioId);
    }

    /**
     * Update database with the latest currency prices from the api service
     */
    public void fetchCurrencyPrices(){
        // Don't execute API calls if rate limit is applied
        if (!priceApiRateLimit.shouldFetch(Integer.toString(1))) { return; }

        // Observe callback and save to db if needed
        CoinmarketgapService.PricesResponseListener listener = new CoinmarketgapService.PricesResponseListener() {

            @Override
            public void onCurrenciesUpdated(ArrayList<Currency> currencies) {
                AsyncTask.execute(() -> mDatabase.currencyDao().insertAll(currencies));
            }

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

    /**
     * Fetches wallet data from api service
     * @param wallets Wallets containing credentials
     */
    public void fetchWallets(List<WalletWithRelations> wallets, ErrorListener errorListener){
        for (WalletWithRelations wallet: wallets) {
            fetchWallet(wallet, errorListener);
        }
    }

    /**
     * Fetches wallet data from api service
     * @param wallet Wallets containing credentials
     */
    private void fetchWallet(WalletWithRelations wallet, ErrorListener errorListener) {
        // Don't execute API calls if rate limit is applied
        if (!apiRateLimit.shouldFetch(Integer.toString(wallet.wallet.getId()))) { return; }

        // Observe callback and save to db if needed
        ApiService.AssetResponseListener listener = new ApiService.AssetResponseListener() {
            @Override
            public void onAssetsUpdated(ArrayList<Asset> assets) {
                for (Asset asset : assets) {
                    AsyncTask.execute(() -> mDatabase.assetDao().insert(asset));
                }
            }

            @Override
            public void onError(String message) {
                String origin;
                if (wallet.getExchangeName() != null) {
                    origin = wallet.getExchangeName();
                } else {
                    origin = wallet.getExchangeName();
                }
                errorListener.onError(origin + ": " + message);
            }
        };

        // Create ApiService
        ApiService.Factory apiServiceFactory = new ApiService.Factory(wallet, listener);
        ApiService apiService = apiServiceFactory.create();

        // Fetch data
        apiService.fetch();
    }

    public interface ErrorListener {
        void onError(String message);
    }
}