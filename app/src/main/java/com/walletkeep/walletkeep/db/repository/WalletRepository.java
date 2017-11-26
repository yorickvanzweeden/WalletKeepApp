package com.walletkeep.walletkeep.db.repository;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Coin;
import com.walletkeep.walletkeep.db.entity.Exchange;
import com.walletkeep.walletkeep.db.entity.Wallet;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

import java.util.ArrayList;
import java.util.List;

public class WalletRepository {
    // Repository instance
    private static WalletRepository sInstance;

    // Database instance
    private final AppDatabase mDatabase;

    /**
     * Constructor: Initializes repository with database
     * @param database Database to use
     */
    public WalletRepository(AppDatabase database) {
        mDatabase = database;
    }

    /**
     * Gets instance of the repository (singleton)
     * @param database Database to use
     * @return Instance of the repository
     */
    public static WalletRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (WalletRepository.class) {
                if (sInstance == null) {
                    sInstance = new WalletRepository(database);
                }
            }
        }
        return sInstance;
    }



    public LiveData<Wallet> getWallet(int walletId) {
        return mDatabase.walletDao().getById(walletId);
    }

    public LiveData<List<WalletWithRelations>> getWallets(int portfolioId) {
        return mDatabase.walletDao().getAll(portfolioId);
    }

    public LiveData<List<Exchange>> getExchangeNames(){
        return mDatabase.exchangeDao().getAll();
    }

    public LiveData<Exchange> getExchangeName(){
        return mDatabase.exchangeDao().getById(1);
    }

    public LiveData<List<Coin>> getPortfolioCoins(int portfolioId){
        return mDatabase.coinDao().getByPortfolioId(portfolioId);
    }

    public void addWallet(Wallet wallet) {
        List<Wallet> walletList = new ArrayList<>();
        walletList.add(wallet);
        AsyncTask.execute(() -> mDatabase.walletDao().insertAll(walletList));
    }

    public void addWallets(List<Wallet> wallets) {
        AsyncTask.execute(() -> mDatabase.walletDao().insertAll(wallets));
    }
}