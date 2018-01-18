package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.walletkeep.walletkeep.AppExecutors;
import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.ResponseHandler;
import com.walletkeep.walletkeep.api.data.CryptoCompareService;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.AggregatedAsset;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.CurrencyPrice;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.di.component.ApiServiceComponent;
import com.walletkeep.walletkeep.di.component.DaggerApiServiceComponent;
import com.walletkeep.walletkeep.di.module.ApiServiceModule;
import com.walletkeep.walletkeep.util.DeltaCalculation;
import com.walletkeep.walletkeep.util.RateLimiter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AssetRepository {
    // Database instance
    private final AppDatabase database;
    private final AppExecutors executors;

    // Rate limiter prevent too many requests
    private RateLimiter<String> priceApiRateLimit = new RateLimiter<>(10, TimeUnit.SECONDS);
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
    public void fetchPrices(@NonNull List<String> currencies, ErrorListener errorListener, boolean delete){
        // Don't execute API calls if rate limit is applied
        if (!priceApiRateLimit.shouldFetch(Integer.toString(1))) { return; }

        // Don't execute API calls if no currencies are provided
        if(currencies.size() == 0) return;

        // Observe callback and save to db if needed
        CryptoCompareService.PricesResponseListener listener = new CryptoCompareService.PricesResponseListener() {

            @Override
            public void onPricesUpdated(List<CurrencyPrice> prices, Boolean delete) {
                Date newdate = new Date();

                executors.diskIO().execute(() -> {
                    if (delete) database.currencyPriceDao().deleteAll();
                    for (CurrencyPrice price: prices) price.setLastUpdated(newdate);
                    database.currencyPriceDao().insertAll(prices);
                });
            }

            @Override
            public void onError(String message) {
                errorListener.onError("Error fetching prices: " + message);
            }
        };
        CryptoCompareService service = new CryptoCompareService(listener);

        // Fetch data
        service.fetch(currencies, delete);
    }

    /**
     * Fetches wallet data from api service
     * @param wallets Wallets containing credentials
     */
    public void fetchWallets(List<WalletWithRelations> wallets, ErrorListener errorListener) {
        // Test internet connection
        executors.networkIO().execute(() -> {
            boolean internet = true;
            try { InetAddress.getByName("www.google.com"); }
            catch (UnknownHostException e) { internet = false; }

            if (!internet)
                executors.mainThread().execute(() -> errorListener.onError("No Internet connection"));
            else
                for (WalletWithRelations wallet : wallets)
                    if (wallet.getType() != WalletWithRelations.Type.Transaction)
                        fetchWallet(wallet, errorListener);
            });
    }
    private void fetchWallet(WalletWithRelations wallet, ErrorListener errorListener) {
        // Don't execute API calls if rate limit is applied
        if (!apiRateLimit.shouldFetch(Integer.toString(wallet.wallet.getId()))) { return; }

        // Observe callback and save to db if needed
        ResponseHandler.ResponseListener listener = new ResponseHandler.ResponseListener() {
            @Override
            public void onAssetsUpdated(ArrayList<Asset> assets) {
                if (assets != null){

                    assets = DeltaCalculation.get(wallet.assets, assets);
                    // In case assets are not updated
                    if (assets == null) return;

                    // Do versioning
                    Date timestamp = new Date();
                    for (Asset asset : assets) asset.setTimestamp(timestamp);
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