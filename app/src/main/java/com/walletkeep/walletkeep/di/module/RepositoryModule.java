package com.walletkeep.walletkeep.di.module;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.di.scope.AppScope;
import com.walletkeep.walletkeep.repository.AssetRepository;
import com.walletkeep.walletkeep.repository.PortfolioRepository;
import com.walletkeep.walletkeep.repository.WalletRepository;

import dagger.Module;
import dagger.Provides;

@Module(includes = DatabaseModule.class)
public class RepositoryModule {

    @AppScope
    @Provides
    public PortfolioRepository portfolioRepository(AppDatabase appDatabase) {
        return new PortfolioRepository(appDatabase);
    }

    @AppScope
    @Provides
    public WalletRepository walletRepository(AppDatabase appDatabase) {
        return new WalletRepository(appDatabase);
    }

    @AppScope
    @Provides
    public AssetRepository assetRepository(AppDatabase appDatabase) {
        return new AssetRepository(appDatabase);
    }
}