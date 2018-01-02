package com.walletkeep.walletkeep.di;

import com.walletkeep.walletkeep.db.AppDatabase;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    private final AppDatabase appDatabase;

    public DatabaseModule(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    @Provides
    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

}
