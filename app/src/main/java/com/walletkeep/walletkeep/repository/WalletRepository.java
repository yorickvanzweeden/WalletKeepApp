package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;

import com.walletkeep.walletkeep.AppExecutors;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.Wallet;
import com.walletkeep.walletkeep.db.entity.WalletToken;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

import java.util.List;

public class WalletRepository {
    // Repository instance
    private static WalletRepository sInstance;

    // Database instance
    private final AppDatabase database;
    private final AppExecutors executors;

    /**
     * Constructor: Initializes repository with database
     * @param database Database to use
     */
    public WalletRepository(AppDatabase database, AppExecutors executors) {
        this.database = database;
        this.executors = executors;
    }

    /**
     * Gets the wallets of a portfolio
     * @param portfolioId Id of the portfolio
     * @return List of wallets of a portfolio
     */
    public LiveData<List<WalletWithRelations>> getWallets(int portfolioId) {
        return database.walletDao().getAll(portfolioId);
    }

    /**
     * Gets specified wallet by id
     * @param walletId Id of the wallet
     * @return Wallet
     */
    public LiveData<WalletWithRelations> getWallet(int walletId) {
        return database.walletDao().getById(walletId);
    }

    /**
     * Add a wallet with relations to the database
     * @param wallet Wallet with relations
     */
    public void addWalletWithRelations(WalletWithRelations wallet) {
        executors.diskIO().execute(() -> database.walletDao().insertWalletWithRelations(wallet));
    }

    /**
     * Updates a wallet in the database
     * @param wallet Updated wallet
     */
    public void updateWallet(Wallet wallet) {
        executors.diskIO().execute(() -> database.walletDao().update(wallet));
    }

    /**
     * Deletes a wallet in the database
     * @param wallet Wallet to delete
     */
    public void deleteWallet(Wallet wallet) {
        executors.diskIO().execute(() -> database.walletDao().delete(wallet));
    }

    /**
     * Updates credentials in the database
     * @param credentials Updated credentials
     */
    public void updateCredentials(ExchangeCredentials credentials) {
        executors.diskIO().execute(() -> database.exchangeCredentialsDao().update(credentials));
    }

    /**
     * Updates token in the database
     * @param token Updated token
     */
    public void insertToken(WalletToken token) {
        executors.diskIO().execute(() -> database.walletTokenDao().insert(token));
    }

    /**
     * Deletes token in the database
     * @param token Token to delete
     */
    public void deleteToken(WalletToken token) {
        executors.diskIO().execute(() -> database.walletTokenDao().delete(token));
    }
}