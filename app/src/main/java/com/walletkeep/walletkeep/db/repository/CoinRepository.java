package com.walletkeep.walletkeep.db.repository;

import android.arch.lifecycle.LiveData;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Coin;

import java.util.ArrayList;
import java.util.List;

public class CoinRepository {
    private static CoinRepository sInstance;
    private final AppDatabase mDatabase;

    public CoinRepository(AppDatabase database) {
        mDatabase = database;
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
        mDatabase.coinDao().insertAll(coinList);
    }

    public void addCoins(List<Coin> coins) {
        mDatabase.coinDao().insertAll(coins);
    }


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
}