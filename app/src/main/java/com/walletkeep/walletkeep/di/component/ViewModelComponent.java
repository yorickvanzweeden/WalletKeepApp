package com.walletkeep.walletkeep.di.component;

import com.walletkeep.walletkeep.di.module.ViewModelModule;
import com.walletkeep.walletkeep.di.scope.ActivityScope;
import com.walletkeep.walletkeep.viewmodel.AssetViewModel;
import com.walletkeep.walletkeep.viewmodel.PortfolioViewModel;
import com.walletkeep.walletkeep.viewmodel.UpdateWalletViewModel;
import com.walletkeep.walletkeep.viewmodel.WalletViewModel;

import dagger.Component;

@ActivityScope
@Component(modules = ViewModelModule.class, dependencies = RepositoryComponent.class)
public interface ViewModelComponent {

    PortfolioViewModel getPortfolioViewModel();

    WalletViewModel getWalletViewModel();

    AssetViewModel getAssetViewModel();

    UpdateWalletViewModel getUpdateWalletViewModel();

}
