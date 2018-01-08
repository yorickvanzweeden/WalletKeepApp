package com.walletkeep.walletkeep.viewmodel;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.walletkeep.walletkeep.db.entity.AggregatedAsset;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.repository.AssetRepository;

import java.util.ArrayList;
import java.util.List;

public class AssetViewModel extends ViewModel {
    private LiveData<List<AggregatedAsset>> aggregatedAssets;
    private LiveData<List<WalletWithRelations>> wallets;
    private AssetRepository assetRepository;

    /**
     * Constructor: Sets repository
     * @param assetRepository Repository to interact with database
     */
    public AssetViewModel(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
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
    }

    /**
     * Loads all the asset (async)
     * @return Livedata list of assets
     */
    public LiveData<List<AggregatedAsset>> getAggregatedAssets() { return aggregatedAssets; }

    public LiveData<List<WalletWithRelations>> getWallets() { return wallets; }

    /**
     * Update all wallets
     */
    public void assetFetch(List<WalletWithRelations> wallets, AssetRepository.ErrorListener errorListener){
        if (wallets != null) {
            this.assetRepository.fetchWallets(wallets, errorListener);
        }
    }

    /**
     * Update all prices
     */
    public void priceFetch(List<AggregatedAsset> assets, AssetRepository.ErrorListener errorListener) {
        if (assets != null) {
            ArrayList<String> currencies = new ArrayList<String>() {{
                for (AggregatedAsset asset:assets) add(asset.currencyTicker);
            }};
            if (currencies.size() != 0) this.assetRepository.fetchPrices(currencies, errorListener);
        }
    }
}
