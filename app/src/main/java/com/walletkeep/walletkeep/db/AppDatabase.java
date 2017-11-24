package com.walletkeep.walletkeep.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.walletkeep.walletkeep.AppExecutors;
import com.walletkeep.walletkeep.db.dao.PortfolioDao;
import com.walletkeep.walletkeep.db.entity.Portfolio;

import java.util.List;

@Database(entities = {Portfolio.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    // Entity dao's
    public abstract PortfolioDao portfolioDao();

    // Database instance
    private static AppDatabase sInstance;

    @VisibleForTesting
    public static final String DATABASE_NAME = "walletkeep-db";

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    /**
     * Gets instance of the database (singleton)
     * @param context
     * @param executors
     * @return Database instance
     */
    public static AppDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext(), executors);
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * Build the database when accessed for the first time
     * @param appContext
     * @param executors
     * @return Database that was built
     */
    private static AppDatabase buildDatabase(final Context appContext,
                                             final AppExecutors executors) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(() -> {

                            // Generate the data for pre-population
                            AppDatabase database = AppDatabase.getInstance(appContext, executors);
                            final List<Portfolio> portfolios = DataGenerator.generatePortfolios();
                            database.runInTransaction(() -> {
                                database.portfolioDao().insertAll(portfolios);
                            });

                            // notify that the database was created and it's ready to be used
                            database.setDatabaseCreated();
                        });
                    }
                }).build();
    }

    /**
     * Check if database exists
     * @param context
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    /**
     * Set database created flag
     */
    private void setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true);
    }

    /**
     * Gets database that was created
     * @return
     */
    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }
}