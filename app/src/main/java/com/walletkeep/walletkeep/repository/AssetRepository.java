package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;

import com.walletkeep.walletkeep.AppExecutors;
import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.ResponseHandler;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.AggregatedAsset;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.Currency;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.di.component.ApiServiceComponent;
import com.walletkeep.walletkeep.di.component.DaggerApiServiceComponent;
import com.walletkeep.walletkeep.di.module.ApiServiceModule;
import com.walletkeep.walletkeep.util.DeltaCalculation;
import com.walletkeep.walletkeep.util.RateLimiter;
import com.walletkeep.walletkeep.util.SynchronisedTicket;

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
            else {
                SynchronisedTicket ticket = new SynchronisedTicket<Asset>(wallets.size());
                for (WalletWithRelations wallet : wallets)
                    if (wallet.getType() != WalletWithRelations.Type.Transaction)
                        fetchWallet(wallet, errorListener, ticket);
                    else
                        ticket.isLast(); // Increase counter

            }
        });
    }
    private void fetchWallet(WalletWithRelations wallet, ErrorListener errorListener, SynchronisedTicket ticket) {
        // Don't execute API calls if rate limit is applied
        if (!apiRateLimit.shouldFetch(Integer.toString(wallet.wallet.getId()))) { return; }

        // Observe callback and save to db if needed
        ResponseHandler.ResponseListener listener = new ResponseHandler.ResponseListener() {
            @Override
            public void onAssetsUpdated(ArrayList<Asset> assets) {
                if (assets != null){

                    assets = DeltaCalculation.get(wallet.assets, assets);
                    // In case assets are not updated
                    if (assets != null) {
                        // Do versioning
                        Date timestamp = new Date();
                        for (Asset asset : assets) asset.setTimestamp(timestamp);
                        ticket.add(assets);
                    }
                    insertAssetsOnLastWallet();
                }
            }

            @Override
            public void onError(String message) {
                String origin = wallet.getExchangeName() == null ? wallet.getAddressCurrency() : wallet.getExchangeName();
                errorListener.onError(origin + ": " + message);
                insertAssetsOnLastWallet();
            }

            void insertAssetsOnLastWallet() {
                if (ticket.isLast()) executors.diskIO().execute(() -> {
                    try { database.assetDao().insertAll((ArrayList<Asset>) ticket.get()); }
                    catch (Exception e) {
                        try {
                            for (Asset asset : (ArrayList<Asset>) ticket.get()) {
                                database.currencyDao().insert(new Currency(asset.getCurrencyTicker()+ "**", asset.getCurrencyTicker()));
                            }
                            database.assetDao().insertAll((ArrayList<Asset>) ticket.get());
                        } catch (Exception f) {
                            errorListener.onError("Could not save assets");
                        }
                    }
                });
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