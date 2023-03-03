package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.Info;

@Dao
public interface InfoDao {
    @Query("SELECT * FROM Info")
    Info getInfo();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Info info);

    @Query("SELECT count(*) from Info")
    int getCount();

    @Query("UPDATE Info SET password =:password ,selectId =:selectId , profile =:profile ,license =:license")
    void update(String password,String selectId,String profile,String license);
}
