package com.walletkeep.walletkeep.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.walletkeep.walletkeep.db.entity.Portfolio;

import java.util.List;

@Dao
public abstract class PortfolioDao implements BaseDao<Portfolio> {
    @Query("SELECT * FROM portfolio")
    public abstract LiveData<List<Portfolio>> getAll();

    @Query("SELECT * FROM portfolio WHERE id LIKE :id LIMIT 1")
    public abstract LiveData<Portfolio> getById(int id);
}