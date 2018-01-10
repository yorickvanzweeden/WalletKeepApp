package com.walletkeep.walletkeep.di.module;

import android.content.Context;

import com.walletkeep.walletkeep.AppExecutors;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.di.scope.AppScope;

import dagger.Module;
import dagger.Provides;

@Module(includes = ContextModule.class)
public class DatabaseModule {

    @AppScope @Provides
    AppExecutors appExecutors() {
        return new AppExecutors();
    }

    @AppScope @Provides
    AppDatabase appDatabase(Context appContext, AppExecutors appExecutors) {
        /* Note: RoomDatabase.Callback does not give access to the RoomDatabase. Therefore it's
         * necessary to create a singleton of the database first and return the instance.
         */
        return AppDatabase.getInstance(appContext, appExecutors);
    }
}
