package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.Wallet;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

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
        this.mDatabase = database;
    }

    /**
     * Gets the wallets of a portfolio
     * @param portfolioId Id of the portfolio
     * @return List of wallets of a portfolio
     */
    public LiveData<List<WalletWithRelations>> getWallets(int portfolioId) {
        return mDatabase.walletDao().getAll(portfolioId);
    }

    /**
     * Gets specified wallet by id
     * @param walletId Id of the wallet
     * @return Wallet
     */
    public LiveData<WalletWithRelations> getWallet(int walletId) {
        return mDatabase.walletDao().getById(walletId);
    }

    /**
     * Add a wallet with relations to the database
     * @param wallet Wallet with relations
     */
    public void addWalletWithRelations(WalletWithRelations wallet) {
        AsyncTask.execute(() -> mDatabase.walletDao().insertWalletWithRelations(wallet));
    }

    /**
     * Updates a wallet in the database
     * @param wallet Updated wallet
     */
    public void updateWallet(Wallet wallet) {
        AsyncTask.execute(() -> mDatabase.walletDao().update(wallet));
    }

    /**
     * Deletes a wallet in the database
     * @param wallet Wallet to delete
     */
    public void deleteWallet(Wallet wallet) {
        AsyncTask.execute(() -> mDatabase.walletDao().delete(wallet));
    }

    /**
     * Updates credentials in the database
     * @param credentials Updated credentials
     */
    public void updateCredentials(ExchangeCredentials credentials) {
        AsyncTask.execute(() -> mDatabase.exchangeCredentialsDao().update(credentials));
    }
}