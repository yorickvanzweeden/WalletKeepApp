package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;

import com.walletkeep.walletkeep.AppExecutors;
import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.ResponseHandler;
import com.walletkeep.walletkeep.api.data.CoinmarketgapService;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.AggregatedAsset;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.Currency;
import com.walletkeep.walletkeep.db.entity.CurrencyPrice;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.di.component.ApiServiceComponent;
import com.walletkeep.walletkeep.di.component.DaggerApiServiceComponent;
import com.walletkeep.walletkeep.di.module.ApiServiceModule;
import com.walletkeep.walletkeep.util.DeltaCalculation;
import com.walletkeep.walletkeep.util.RateLimiter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AssetRepository {
    // Repository instance
    private static AssetRepository sInstance;

    // Database instance
    private final AppDatabase database;
    private final AppExecutors executors;

    // Rate limiter prevent too many requests
    private RateLimiter<String> priceApiRateLimit = new RateLimiter<>(3, TimeUnit.MINUTES);
    private RateLimiter<String> apiRateLimit = new RateLimiter<>(10, TimeUnit.SECONDS);


    /**
     * Constructor: Initializes repository with database
     * @param database Database to use
     */
    public AssetRepository(AppDatabase database, AppExecutors executors) {
        this.database = database;
        this.executors = executors;
    }


    /**
     * Gets a list of aggregated assets of a portfolio
     * @param portfolioId Id of the portfolio
     * @return List of aggregated assets
     */
    public LiveData<List<AggregatedAsset>> getAggregatedAssets(int portfolioId) {
        return database.assetDao().getAggregatedAssets(portfolioId);
    }

    /**
     * Gets a list of aggregated assets of a portfolio
     * @param portfolioId Id of the portfolio
     * @return List of aggregated assets
     */
    public LiveData<List<WalletWithRelations>> getWallets(int portfolioId) {
        return database.walletDao().getAll(portfolioId);
    }

    /**
     * Update database with the latest currency prices from the api service
     */
    public void fetchCurrencyPrices(ErrorListener errorListener){
        // Don't execute API calls if rate limit is applied
        if (!priceApiRateLimit.shouldFetch(Integer.toString(1))) { return; }

        // Observe callback and save to db if needed
        CoinmarketgapService.PricesResponseListener listener = new CoinmarketgapService.PricesResponseListener() {

            @Override
            public void onCurrenciesUpdated(ArrayList<Currency> currencies) {
                executors.diskIO().execute(() -> database.currencyDao().insertAll(currencies));
            }

            @Override
            public void onPricesUpdated(ArrayList<CurrencyPrice> prices) {
                executors.diskIO().execute(() -> database.currencyPriceDao().insertAll(prices));
            }

            @Override
            public void onError(String message) {
                errorListener.onError("Error fetching prices: " + message);
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
            if (wallet.getType() != WalletWithRelations.Type.Transaction) {
                fetchWallet(wallet, errorListener);
            }
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
        ResponseHandler.ResponseListener listener = new ResponseHandler.ResponseListener() {
            @Override
            public void onAssetsUpdated(ArrayList<Asset> assets) {
                if ((wallet.assets == null & assets != null) || !wallet.assets.equals(assets)){

                    assets = DeltaCalculation.get(wallet.assets, assets);

                    // Do versioning
                    Date timestamp = new Date();
                    for (Asset asset: assets) {
                        asset.setTimestamp(timestamp);
                    }
                    for (Asset asset : assets) executors.diskIO().execute(() -> database.assetDao().insert(asset));
                }
            }

            @Override
            public void onError(String message) {
                String origin = wallet.getExchangeName() == null ? wallet.getAddressCurrency() : wallet.getExchangeName();
                errorListener.onError(origin + ": " + message);
            }
        };

        // Create ApiService
        ApiServiceComponent component = DaggerApiServiceComponent.builder()
                .apiServiceModule(new ApiServiceModule(wallet, listener))
                .build();
        ApiService apiService = component.getApiService();

        // Fetch data
        apiService.fetch();
    }

    public interface ErrorListener {
        void onError(String message);
    }
}