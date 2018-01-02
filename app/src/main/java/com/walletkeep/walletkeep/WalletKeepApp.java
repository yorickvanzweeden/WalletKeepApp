package com.walletkeep.walletkeep;

import android.app.Application;

import com.walletkeep.walletkeep.di.component.DaggerRepositoryComponent;
import com.walletkeep.walletkeep.di.component.RepositoryComponent;
import com.walletkeep.walletkeep.di.module.ContextModule;

public class WalletKeepApp extends Application {

    private RepositoryComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerRepositoryComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
    }

    public RepositoryComponent component() {
        return component;
    }
}
