package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.LicenseStampCheck;

import java.util.List;

@Dao
public interface LicenseStampCheckDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LicenseStampCheck addLicense);

    @Query("SELECT stampChk FROM LicenseStampCheck WHERE license_num =:num ")
    boolean getStampChk(String num);

    @Query("SELECT * FROM LicenseStampCheck")
    List<LicenseStampCheck> getAllStampChk();

    @Query("DELETE  FROM LicenseStampCheck")
    void delete();

    @Query("UPDATE LicenseStampCheck SET stampChk = 1 WHERE license_num =:license_num AND licenseState =:licenseState")
    void update(String license_num,String licenseState);
}
