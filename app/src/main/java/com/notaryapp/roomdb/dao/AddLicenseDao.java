package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.notaryapp.roomdb.entity.AddLicense;

import java.util.List;


@Dao
public interface AddLicenseDao {

    @Query("SELECT * FROM AddLicense")
    List<AddLicense> getLicense();

    @Query("SELECT count(*) FROM AddLicense WHERE licenseNum =:num")
    int getCount(String  num);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AddLicense addLicense);

    @Delete
    void delete(AddLicense addLicense);

    @Query("DELETE  FROM AddLicense")
    void delete();

    @Query("SELECT licenseNum FROM AddLicense")
    String getLicenseNum();

    @Query("SELECT count(*) FROM AddLicense")
    int getNetCount();

    @Update
    void update(AddLicense addLicense);

    @Query("UPDATE AddLicense SET address =:address ,licenseState =:licenseState,city =:city, phone =:phn,zip =:zip, state =:state, expiryDate =:expiryDate WHERE licenseNum =:num")
    void update(String licenseState,String address,String city,String phn,String state,String zip,String num, String expiryDate);
}
