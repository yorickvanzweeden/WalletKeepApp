package com.walletkeep.walletkeep.repository;

import android.arch.lifecycle.LiveData;

import com.walletkeep.walletkeep.AppExecutors;
import com.walletkeep.walletkeep.db.AppDatabase;
import com.walletkeep.walletkeep.db.entity.WalletTokenA;

import java.util.List;

public class TokenRepository {
    // Database instance
    private final AppDatabase database;
    private final AppExecutors executors;

    /**
     * Constructor: Initializes repository with database
     * @param database Database to use
     */
    public TokenRepository(AppDatabase database, AppExecutors executors) {
        this.database = database;
        this.executors = executors;
    }

    /**
     * Gets the tokens of a wallet
     * @param walletId Id of the wallet
     * @return List of tokens of a wallet
     */
    public LiveData<List<WalletTokenA>> getTokens(int walletId) {
        return database.walletTokenDao().getByWalletId(walletId);
    }

    public void insertTokens(List<WalletTokenA> tokens) {
        executors.diskIO().execute(() -> database.walletTokenDao().insertTokens(tokens));
    }

    public void deleteTokens(List<WalletTokenA> tokens) {
        executors.diskIO().execute(() -> database.walletTokenDao().deleteTokens(tokens));
    }
}
