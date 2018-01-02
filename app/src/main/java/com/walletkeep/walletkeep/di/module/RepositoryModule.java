package com.walletkeep.walletkeep.di.module;

import com.walletkeep.walletkeep.AppExecutors;
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
    public PortfolioRepository portfolioRepository(AppDatabase appDatabase, AppExecutors appExecutors) {
        return new PortfolioRepository(appDatabase, appExecutors);
    }

    @AppScope
    @Provides
    public WalletRepository walletRepository(AppDatabase appDatabase, AppExecutors appExecutors) {
        return new WalletRepository(appDatabase, appExecutors);
    }

    @AppScope
    @Provides
    public AssetRepository assetRepository(AppDatabase appDatabase, AppExecutors appExecutors) {
        return new AssetRepository(appDatabase, appExecutors);
    }
}