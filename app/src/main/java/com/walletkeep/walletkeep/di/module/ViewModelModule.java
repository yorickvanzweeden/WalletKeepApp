package com.walletkeep.walletkeep.di.module;


import com.walletkeep.walletkeep.di.scope.ActivityScope;
import com.walletkeep.walletkeep.repository.AssetRepository;
import com.walletkeep.walletkeep.repository.PortfolioRepository;
import com.walletkeep.walletkeep.repository.WalletRepository;
import com.walletkeep.walletkeep.viewmodel.AssetViewModel;
import com.walletkeep.walletkeep.viewmodel.PortfolioViewModel;
import com.walletkeep.walletkeep.viewmodel.UpdateWalletViewModel;
import com.walletkeep.walletkeep.viewmodel.WalletViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class ViewModelModule {

    @ActivityScope
    @Provides
    PortfolioViewModel portfolioViewModel(PortfolioRepository portfolioRepository) {
        return new PortfolioViewModel(portfolioRepository);
    }

    @ActivityScope
    @Provides
    WalletViewModel walletViewModel(WalletRepository walletRepository) {
        return new WalletViewModel(walletRepository);
    }

    @ActivityScope
    @Provides
    AssetViewModel assetViewModel(AssetRepository assetRepository) {
        return new AssetViewModel(assetRepository);
    }

    @ActivityScope
    @Provides
    UpdateWalletViewModel updateWalletViewModel(WalletRepository walletRepository) {
        return new UpdateWalletViewModel(walletRepository);
    }
}
