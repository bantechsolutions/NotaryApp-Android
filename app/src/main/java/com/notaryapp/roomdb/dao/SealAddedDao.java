package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.SealAdded;

@Dao
public interface SealAddedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SealAdded sealAdded);

    @Query("SELECT * FROM SealAdded")
    SealAdded getAllLicense();

    @Query("DELETE from SealAdded")
    int deleteAll();

}
