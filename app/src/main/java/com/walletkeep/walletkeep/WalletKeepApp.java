package com.walletkeep.walletkeep;

import android.app.Application;
import android.content.Context;

public class WalletKeepApp extends Application {

    private AppExecutors mAppExecutors;
    private static Context mContext;

    public static Context getContext(){
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppExecutors = new AppExecutors();
        mContext = this;
    }
}
