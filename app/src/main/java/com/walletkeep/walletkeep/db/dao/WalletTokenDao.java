package com.walletkeep.walletkeep.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.walletkeep.walletkeep.db.entity.WalletToken;
import com.walletkeep.walletkeep.db.entity.WalletTokenA;

import java.util.List;

@Dao
public abstract class WalletTokenDao implements BaseDao<WalletToken> {
    @Query("SELECT * FROM wallettoken WHERE wallet_id LIKE :walletId")
    public abstract LiveData<List<WalletTokenA>> getByWalletId(int walletId);

    @Insert
    public void insertTokens(List<WalletTokenA> tokens) {
        for(WalletTokenA token: tokens) insert(token.token);
    }

    @Delete
    public void deleteTokens(List<WalletTokenA> tokens) {
        for(WalletTokenA token: tokens) delete(token.token);
    }
}
