package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;

import com.walletkeep.walletkeep.AppExecutors;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.Asset;
import com.walletkeep.walletkeep.db.entity.Currency;
import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.Wallet;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

import java.util.List;

public class WalletRepository {
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
        executors.diskIO().execute(() -> {
            try {
                database.walletDao().insertWalletWithRelations(wallet);
            } catch (Exception e) {
                for (int i = 0; i < wallet.assets.size(); i++) {
                    database.currencyDao().insert(
                            new Currency(wallet.assets.get(i).getCurrencyTicker(),
                                    wallet.assets.get(i).getCurrencyTicker())
                    );
                    database.assetDao().insertAll(wallet.assets);
                }
            }
        });
    }

    /**
     * Updates a wallet in the database
     * @param wallet Updated wallet
     */
    public void updateWallet(Wallet wallet) {
        executors.diskIO().execute(() -> database.walletDao().update(wallet));
    }

    public void updateAssets(List<Asset> assets) {
        try {
            executors.diskIO().execute(() -> database.assetDao().insertAll(assets));
        } catch (Exception e) {
            for (Asset asset: assets) {
                database.currencyDao().insert(new Currency(asset.getCurrencyTicker(),asset.getCurrencyTicker()));
            }
            executors.diskIO().execute(() -> database.assetDao().insertAll(assets));
        }
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
}