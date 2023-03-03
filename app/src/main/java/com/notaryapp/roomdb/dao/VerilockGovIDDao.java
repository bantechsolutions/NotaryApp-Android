package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.notaryapp.roomdb.entity.ValidateId_IdentityType;

import java.util.List;

@Dao
public interface NotaryAppGovIDDao {

    @Insert
    void insert(ValidateId_IdentityType identityType);

    @Delete
    void delete(ValidateId_IdentityType identityType);

    @Update
    void update(ValidateId_IdentityType identityType);

     @Query("UPDATE ValidateId_IdentityType SET selectedType = :selectIdType ")
      void update(String selectIdType);

    @Query("SELECT selectedType FROM ValidateId_IdentityType")
    String getSelectIdType();

    @Query("SELECT * FROM ValidateId_IdentityType ")
    ValidateId_IdentityType getInfo();

    @Query("SELECT selectedType FROM ValidateId_IdentityType")
    List<String> getDeletedType();

//    @Query("UPDATE ValidateId_IdentityType SET typeId =:id")
//    void update(int id);

//    @Query("UPDATE ValidateId_IdentityType SET deletedFlag =1 WHERE selectedType =:type")
//    void updateDelete(String type);

    @Query("DELETE FROM ValidateId_IdentityType")
    void deleteAll();

    @Query("SELECT count(*) FROM ValidateId_IdentityType")
    int deletedCount();
}
