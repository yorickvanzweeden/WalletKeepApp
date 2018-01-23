package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.walletkeep.walletkeep.AppExecutors;
import com.walletkeep.walletkeep.api.ApiService;
import com.walletkeep.walletkeep.api.ResponseHandler;
import com.walletkeep.walletkeep.api.data.CryptoCompareService;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.AggregatedAsset;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.Currency;
import com.walletkeep.walletkeep.db.entity.CurrencyPrice;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;
import com.walletkeep.walletkeep.di.component.ApiServiceComponent;
import com.walletkeep.walletkeep.di.component.DaggerApiServiceComponent;
import com.walletkeep.walletkeep.di.module.ApiServiceModule;
import com.walletkeep.walletkeep.util.DeltaCalculation;
import com.walletkeep.walletkeep.util.RateLimiter;
import com.walletkeep.walletkeep.util.SynchronisedTicket;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CurrencyRepository {
    // Database instance
    private final AppDatabase database;
    private final AppExecutors executors;

    /**
     * Constructor: Initializes repository with database
     * @param database Database to use
     */
    public CurrencyRepository(AppDatabase database, AppExecutors executors) {
        this.database = database;
        this.executors = executors;
    }

    public LiveData<Currency> getCurrency(String currencyTicker) {
        return database.currencyDao().getByTicker(currencyTicker);
    }
}