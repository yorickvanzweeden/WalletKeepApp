package com.walletkeep.walletkeep.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.walletkeep.walletkeep.db.entity.Portfolio;

import java.util.List;

@Dao
public interface PortfolioDao {
    @Query("SELECT * FROM portfolio")
    LiveData<List<Portfolio>> loadPortfolios();

    @Query("SELECT * FROM portfolio WHERE id LIKE :id LIMIT 1")
    LiveData<Portfolio> getById(String id);

    @Insert
    void insertAll(List<Portfolio> portfolios);

    @Update
    void update(Portfolio portfolio);

    @Delete
    void delete(Portfolio portfolio);
}