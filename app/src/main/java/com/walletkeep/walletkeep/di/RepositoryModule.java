package com.walletkeep.walletkeep.di;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.repository.PortfolioRepository;

import dagger.Module;
import dagger.Provides;

@Module(includes = DatabaseModule.class)
public class RepositoryModule {

    @Provides
    public PortfolioRepository portfolioRepository(AppDatabase appDatabase) {
        return new PortfolioRepository(appDatabase);
    }
}