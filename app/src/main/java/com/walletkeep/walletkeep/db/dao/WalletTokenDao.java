package com.walletkeep.walletkeep.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.walletkeep.walletkeep.db.entity.WalletToken;
import com.walletkeep.walletkeep.db.entity.WalletTokenWithoutAddress;

import java.util.List;

@Dao
public abstract class WalletTokenDao implements BaseDao<WalletTokenWithoutAddress> {
    @Query("SELECT * FROM WalletTokenWithoutAddress WHERE wallet_id LIKE :walletId")
    public abstract LiveData<List<WalletToken>> getByWalletId(int walletId);

    @Insert
    public void insertTokens(List<WalletToken> tokens) {
        for(WalletToken token: tokens) insert(token.token);
    }

    @Delete
    public void deleteTokens(List<WalletToken> tokens) {
        for(WalletToken token: tokens) delete(token.token);
    }
}
