package com.walletkeep.walletkeep.di.component;

import com.walletkeep.walletkeep.di.module.RepositoryModule;
import com.walletkeep.walletkeep.di.scope.AppScope;
import com.walletkeep.walletkeep.repository.AssetRepository;
import com.walletkeep.walletkeep.repository.PortfolioRepository;
import com.walletkeep.walletkeep.repository.WalletRepository;

import dagger.Component;

@AppScope
@Component(modules = RepositoryModule.class)
public interface RepositoryComponent {

    PortfolioRepository getPortfolioRepository();

    WalletRepository getWalletRepository();

    AssetRepository getAssetRepository();
}
