package com.walletkeep.walletkeep.di;


import com.walletkeep.walletkeep.repository.PortfolioRepository;
import com.walletkeep.walletkeep.viewmodel.PortfolioViewModel;

import dagger.Module;
import dagger.Provides;

@Module(includes = RepositoryModule.class)
public class ViewModelModule {

    @Provides
    public PortfolioViewModel portfolioViewModel(PortfolioRepository portfolioRepository) {
        return new PortfolioViewModel(portfolioRepository);
    }
}
