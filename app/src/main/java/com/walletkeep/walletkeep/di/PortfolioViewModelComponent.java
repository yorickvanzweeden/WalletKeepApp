package com.walletkeep.walletkeep.di;

import com.walletkeep.walletkeep.viewmodel.PortfolioViewModel;

import dagger.Component;

@Component(modules = ViewModelModule.class)
public interface PortfolioViewModelComponent {

    PortfolioViewModel getPortfolioViewModel();
}
