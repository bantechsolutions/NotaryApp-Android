package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.JumioKeys;

@Dao
public interface JumioKeysDao {

    @Query("SELECT * FROM JumioKeys")
    JumioKeys getJumioKeys();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(JumioKeys jumioKeys);

    @Delete
    void delete(JumioKeys jumioKeys);

    @Query("SELECT count(*) from JumioKeys")
    int getCount();

    @Query("UPDATE JumioKeys SET jumiokey =:jumioKey ,jumiosecret =:jumioSecret")
    void update(String jumioKey,String jumioSecret);
}
