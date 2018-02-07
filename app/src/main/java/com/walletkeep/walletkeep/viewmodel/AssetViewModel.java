package com.walletkeep.walletkeep.viewmodel;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.walletkeep.walletkeep.db.entity.AggregatedAsset;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.repository.AssetRepository;
import com.walletkeep.walletkeep.repository.CurrencyRepository;

import java.util.List;

public class AssetViewModel extends ViewModel {
    private LiveData<List<AggregatedAsset>> aggregatedAssets;
    private LiveData<List<WalletWithRelations>> wallets;
    private LiveData<List<String>> currencies;
    private AssetRepository assetRepository;
    private CurrencyRepository currencyRepository;

    /**
     * Constructor: Sets repository
     * @param assetRepository Repository to interact with database
     */
    public AssetViewModel(AssetRepository assetRepository, CurrencyRepository currencyRepository) {
        this.assetRepository = assetRepository;
        this.currencyRepository = currencyRepository;
    }

    /**
     * Gets the assets
     * @param portfolioId Id of the portfolio containing the assets
     */
    public void init(int portfolioId, AssetRepository.ErrorListener errorListener) {
        // Update assets
        if (this.aggregatedAssets == null)
            this.aggregatedAssets = assetRepository.getAggregatedAssets(portfolioId);

        // Update wallets
        if (this.wallets == null)
            this.wallets = assetRepository.getWallets(portfolioId);

        // Update currencies
        if (this.currencies == null)
            this.currencies = currencyRepository.getCurrencies();
    }

    /**
     * Loads all the asset (async)
     * @return Livedata list of assets
     */
    public LiveData<List<AggregatedAsset>> getAggregatedAssets() { return aggregatedAssets; }

    public LiveData<List<WalletWithRelations>> getWallets() { return wallets; }

    public LiveData<List<String>> getCurrencies() { return currencies; }

    /**
     * Update all wallets
     */
    public void assetFetch(List<WalletWithRelations> wallets, AssetRepository.ErrorListener errorListener){
        if (wallets != null) {
            this.assetRepository.fetchWallets(wallets, errorListener);
        }
    }

    /**
     * Subscribe to price changes
     */
    public void subscribePriceChanges(List<String> currencies){
        if (currencies != null && currencies.size() != 0) {
            this.currencyRepository.updateFirebaseWatchList(currencies);
        }
    }
}
