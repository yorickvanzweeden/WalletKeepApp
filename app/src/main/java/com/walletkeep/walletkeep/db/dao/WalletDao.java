package com.walletkeep.walletkeep.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.walletkeep.walletkeep.db.entity.ExchangeCredentials;
import com.walletkeep.walletkeep.db.entity.Wallet;
import com.walletkeep.walletkeep.db.entity.WalletWithRelations;

import java.util.List;

@Dao
public abstract class WalletDao implements BaseDao<Wallet> {
    @Transaction
    @Query("SELECT * FROM wallet WHERE portfolio_id LIKE :portfolioId")
    public abstract LiveData<List<WalletWithRelations>> getAll(int portfolioId);

    @Query("SELECT * FROM wallet WHERE id LIKE :id LIMIT 1")
    public abstract LiveData<WalletWithRelations> getById(int id);

    public void insertWalletWithRelations(WalletWithRelations wallet) {
        long walletId = insertWallet(wallet.wallet);

        // Don't try to save if credentials are null
        if (wallet.getCredentials() == null) return;

        wallet.getCredentials().setWallet_id((int)walletId);
        insertCredentials(wallet.getCredentials());
    }

    @Insert
    public abstract long insertWallet(Wallet wallet);

    @Insert
    public abstract void insertCredentials(ExchangeCredentials exchangeCredentials);
}