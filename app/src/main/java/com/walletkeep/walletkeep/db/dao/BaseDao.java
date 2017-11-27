package com.walletkeep.walletkeep.db.dao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import java.util.List;

interface BaseDao<T> {
    @Insert
    void insertAll(List<T> list);

    @Insert
    void insert(T item);

    @Update
    void update(T item);

    @Delete
    void delete(T item);
}
