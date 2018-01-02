package com.walletkeep.walletkeep.di.component;

import com.walletkeep.walletkeep.di.module.ViewModelModule;
import com.walletkeep.walletkeep.di.scope.AppScope;
import com.walletkeep.walletkeep.viewmodel.AssetViewModel;
import com.walletkeep.walletkeep.viewmodel.PortfolioViewModel;
import com.walletkeep.walletkeep.viewmodel.UpdateWalletViewModel;
import com.walletkeep.walletkeep.viewmodel.WalletViewModel;

import dagger.Component;

@AppScope
@Component(modules = ViewModelModule.class)
public interface ViewModelComponent {

    PortfolioViewModel getPortfolioViewModel();

    WalletViewModel getWalletViewModel();

    AssetViewModel getAssetViewModel();

    UpdateWalletViewModel getUpdateWalletViewModel();

}
