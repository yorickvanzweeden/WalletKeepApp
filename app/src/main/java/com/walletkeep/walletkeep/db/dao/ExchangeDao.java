package com.walletkeep.walletkeep.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.walletkeep.walletkeep.db.entity.Exchange;

import java.util.List;

@Dao
public interface ExchangeDao {
    @Query("SELECT * FROM exchange")
    LiveData<List<Exchange>> getAll();

    @Query("SELECT * FROM exchange WHERE id LIKE :id LIMIT 1")
    LiveData<Exchange> getById(int id);
    
    @Insert
    void insertAll(List<Exchange> exchanges);

    @Update
    void update(Exchange exchange);

    @Delete
    void delete(Exchange exchange);
}
