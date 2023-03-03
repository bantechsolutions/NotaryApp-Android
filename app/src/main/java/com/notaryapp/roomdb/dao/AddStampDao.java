package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.AddStamp;

import java.util.List;

@Dao
public interface AddStampDao {

    @Query("SELECT stampImgPath FROM AddStamp WHERE license_num =:license_no AND licenseState =:licenseState")
    List<String> getStampPaths(String license_no,String licenseState);

    @Query("SELECT stampImgName FROM AddStamp WHERE license_num =:license_no AND licenseState =:licenseState")
    List<String> getStampNames(String license_no,String licenseState);

    @Insert
    void insert(AddStamp addStampe);

    @Query("DELETE  FROM AddStamp")
    void delete();

    @Query("SELECT count(*) FROM AddStamp")
    int getStampCount();

    @Query("SELECT * FROM AddStamp")
    List<AddStamp> getStamps();

    @Query("SELECT COUNT(stampImgName) FROM AddStamp WHERE license_num =:license_no")
    int getImageCount(String license_no);

    @Query("UPDATE AddStamp SET stampImgName =:stampImgName WHERE stampPosition =:position")
    int updateStamp(String stampImgName,int position);
}
