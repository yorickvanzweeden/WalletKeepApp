package com.walletkeep.walletkeep.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.walletkeep.walletkeep.db.entity.WalletToken;

@Dao
public abstract class WalletTokenDao implements BaseDao<WalletToken> {
    @Query("SELECT * FROM wallettoken WHERE wallet_id LIKE :walletId LIMIT 1")
    public abstract LiveData<WalletToken> getByWalletId(int walletId);
}
