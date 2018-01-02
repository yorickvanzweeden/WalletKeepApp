package com.walletkeep.walletkeep;

import android.app.Application;
import android.content.Context;

import com.walletkeep.walletkeep.di.component.DaggerRepositoryComponent;
import com.walletkeep.walletkeep.di.component.RepositoryComponent;
import com.walletkeep.walletkeep.di.module.ContextModule;

public class WalletKeepApp extends Application {

    private AppExecutors mAppExecutors;
    private static Context mContext;
    private RepositoryComponent component;
    public static Context getContext(){
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppExecutors = new AppExecutors();
        mContext = this;

        component = DaggerRepositoryComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
    }

    public RepositoryComponent component() {
        return component;
    }
}
