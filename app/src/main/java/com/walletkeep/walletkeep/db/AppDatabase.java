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
import com.walletkeep.walletkeep.db.dao.AssetDao;
import com.walletkeep.walletkeep.db.dao.CurrencyDao;
import com.walletkeep.walletkeep.db.dao.ExchangeCredentialsDao;
import com.walletkeep.walletkeep.db.dao.ExchangeDao;
import com.walletkeep.walletkeep.db.dao.PortfolioDao;
import com.walletkeep.walletkeep.db.dao.WalletDao;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.Currency;
import com.walletkeep.walletkeep.db.entity.Exchange;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.Portfolio;
import com.walletkeep.walletkeep.db.entity.Wallet;

import java.util.List;

@Database(entities = {
        Asset.class,
        Currency.class,
        Exchange.class,
        ExchangeCredentials.class,
        Portfolio.class,
        Wallet.class
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    // Entity dao's
    public abstract AssetDao assetDao();
    public abstract CurrencyDao currencyDao();
    public abstract ExchangeCredentialsDao exchangeCredentialsDao();
    public abstract ExchangeDao exchangeDao();
    public abstract PortfolioDao portfolioDao();
    public abstract WalletDao walletDao();

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
                            List<Currency> currencies = DataGenerator.generateCurrencies();
                            List<Exchange> exchanges = DataGenerator.generateExchanges();
                            List<Portfolio> portfolios = DataGenerator.generatePortfolios();
                            List<Wallet> wallets = DataGenerator.generateWallets();
                            List<Asset> assets = DataGenerator.generateAssets();

                            database.runInTransaction(() -> {
                                database.currencyDao().insertAll(currencies);
                                database.exchangeDao().insertAll(exchanges);
                                database.portfolioDao().insertAll(portfolios);
                                database.walletDao().insertAll(wallets);
                                database.assetDao().insertAll(assets);
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