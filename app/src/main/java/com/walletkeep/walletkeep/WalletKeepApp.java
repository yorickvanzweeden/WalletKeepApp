package com.walletkeep.walletkeep;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.walletkeep.walletkeep.di.component.DaggerRepositoryComponent;
import com.walletkeep.walletkeep.di.component.RepositoryComponent;
import com.walletkeep.walletkeep.di.module.ContextModule;

public class WalletKeepApp extends Application {

    private static Context mContext;
    private RepositoryComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        mContext = this;
        component = DaggerRepositoryComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
    }

    public static Context context(){
        return mContext;
    }

    public RepositoryComponent component() {
        return component;
    }
}
