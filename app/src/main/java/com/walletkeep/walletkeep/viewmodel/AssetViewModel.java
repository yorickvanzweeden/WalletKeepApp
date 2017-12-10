package com.walletkeep.walletkeep.viewmodel;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.walletkeep.walletkeep.WalletKeepApp;
import com.walletkeep.walletkeep.db.entity.AggregatedAsset;
import com.walletkeep.walletkeep.repository.AssetRepository;

import java.util.List;

public class AssetViewModel extends ViewModel {
    private LiveData<List<AggregatedAsset>> aggregatedAssets;
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
    public void init(int portfolioId) {
        if (this.aggregatedAssets == null)
            this.aggregatedAssets = assetRepository.getAggregatedAssets(portfolioId);
        this.assetRepository.fetchCurrencyPrices();
    }

    /**
     * Loads all the asset (async)
     * @return Livedata list of assets
     */
    public LiveData<List<AggregatedAsset>> getAggregatedAssets() {
        return aggregatedAssets;
    }
    

    /**
     * Returns view model with repository
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final AssetRepository assetRepository;

        public Factory(@NonNull Application application) {
            assetRepository = ((WalletKeepApp) application).getAssetRepository();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new AssetViewModel(assetRepository);
        }
    }
}
