package com.walletkeep.walletkeep.di;


import com.walletkeep.walletkeep.repository.AssetRepository;
import com.walletkeep.walletkeep.repository.PortfolioRepository;
import com.walletkeep.walletkeep.repository.WalletRepository;
import com.walletkeep.walletkeep.viewmodel.AssetViewModel;
import com.walletkeep.walletkeep.viewmodel.PortfolioViewModel;
import com.walletkeep.walletkeep.viewmodel.UpdateWalletViewModel;
import com.walletkeep.walletkeep.viewmodel.WalletViewModel;

import dagger.Module;
import dagger.Provides;

@Module(includes = RepositoryModule.class)
public class ViewModelModule {

    @Provides
    public PortfolioViewModel portfolioViewModel(PortfolioRepository portfolioRepository) {
        return new PortfolioViewModel(portfolioRepository);
    }

    @Provides
    public WalletViewModel walletViewModel(WalletRepository walletRepository) {
        return new WalletViewModel(walletRepository);
    }
    
    @Provides
    public AssetViewModel assetViewModel(AssetRepository assetRepository) {
        return new AssetViewModel(assetRepository);
    }

    @Provides
    public UpdateWalletViewModel updateWalletViewModel(WalletRepository walletRepository) {
        return new UpdateWalletViewModel(walletRepository);
    }
}
