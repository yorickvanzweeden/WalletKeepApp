package com.walletkeep.walletkeep.db.repository;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Coin;

import java.util.ArrayList;
import java.util.List;

public class CoinRepository {
    // Repository instance
    private static CoinRepository sInstance;

    // Database instance
    private final AppDatabase mDatabase;

    /**
     * Constructor: Initializes repository with database
     * @param database Database to use
     */
    public CoinRepository(AppDatabase database) {
        mDatabase = database;
    }

    /**
     * Gets instance of the repository (singleton)
     * @param database Database to use
     * @return Instance of the repository
     */
    public static CoinRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (CoinRepository.class) {
                if (sInstance == null) {
                    sInstance = new CoinRepository(database);
                }
            }
        }
        return sInstance;
    }



    public LiveData<Coin> getCoin(int coinId) {
        return mDatabase.coinDao().getById(coinId);
    }

    public LiveData<List<Coin>> getCoins() {
        return mDatabase.coinDao().getAll();
    }

    public void addCoin(Coin coin) {
        List<Coin> coinList = new ArrayList<>();
        coinList.add(coin);
        AsyncTask.execute(() -> mDatabase.coinDao().insertAll(coinList));
    }

    public void addCoins(List<Coin> coins) {
        AsyncTask.execute(() -> mDatabase.coinDao().insertAll(coins));
    }
}