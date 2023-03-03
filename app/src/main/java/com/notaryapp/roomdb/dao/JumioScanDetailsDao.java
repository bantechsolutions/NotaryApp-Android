package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.JumioScanDetails;

@Dao
public interface JumioScanDetailsDao {

    @Query("SELECT * FROM JumioScanDetails")
    JumioScanDetails getDetails();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(JumioScanDetails scanDettails);

    @Query("SELECT count(*) from JumioScanDetails")
    int getCount();

    @Query("SELECT scanRef from JumioScanDetails")
    String getScanRef();

    @Query("UPDATE JumioScanDetails SET scanRef =:scanRef ,firstName =:firstName , lastName =:lastName ,docType =:docType")
    void update(String scanRef,String firstName,String lastName,String docType);
}
