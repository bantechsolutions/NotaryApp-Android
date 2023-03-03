package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.VaLicense;

@Dao
public interface VaLicenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(VaLicense vaLicense);

    @Query("SELECT * FROM VaLicense")
    VaLicense getAllLicense();

    @Query("SELECT license FROM VaLicense WHERE selectedLice=1")
    String getSelectedLicense();
    @Query("SELECT selectedImgPath FROM VaLicense WHERE selectedLice=1")
    String getSelectedImg();

    @Query("SELECT count(*) from VaLicense WHERE selectedLice=1")
    int getCount();

    @Query("DELETE from VaLicense")
    int deleteAll();

    @Query("UPDATE VaLicense SET selectedLice =1 , selectedImgPath =:imgPath  WHERE license =:licenseNum")
    void update(String licenseNum,String imgPath);
}
