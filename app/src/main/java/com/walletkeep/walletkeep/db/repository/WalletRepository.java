package com.walletkeep.walletkeep.db.repository;

import android.arch.lifecycle.LiveData;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Wallet;

import java.util.ArrayList;
import java.util.List;

public class WalletRepository {
    private static WalletRepository sInstance;
    private final AppDatabase mDatabase;

    public WalletRepository(AppDatabase database) {
        mDatabase = database;
    }

    public LiveData<Wallet> getWallet(int walletId) {
        return mDatabase.walletDao().getById(walletId);
    }

    public LiveData<List<Wallet>> getWallets() {
        return mDatabase.walletDao().getAll();
    }

    public void addWallet(Wallet wallet) {
        List<Wallet> walletList = new ArrayList<>();
        walletList.add(wallet);
        mDatabase.walletDao().insertAll(walletList);
    }

    public void addWallets(List<Wallet> wallets) {
        mDatabase.walletDao().insertAll(wallets);
    }


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
}