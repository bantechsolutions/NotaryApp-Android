package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.ValidateId_IdentityType;

import java.util.List;

@Dao
public interface ValidateId_SelectIDDao {

    @Insert
    void insert(ValidateId_IdentityType identityType);

    @Delete
    void delete(ValidateId_IdentityType identityType);

     /*@Query("UPDATE ValidateId_IdentityType SET typeId = :identityType , selectedType = :selectIdType WHERE deletedFlag= 0")
      void update(int identityType,String selectIdType);

    @Query("SELECT selectedType FROM ValidateId_IdentityType WHERE deletedFlag =0")
    String getSelectIdType();*/

    @Query("SELECT selectedType FROM ValidateId_IdentityType ORDER BY id DESC LIMIT 1")
    String getSelectedType();

    @Query("SELECT selectedType FROM ValidateId_IdentityType")
    List<String> getAllSelectedIds();

    @Query("UPDATE ValidateId_IdentityType SET retryCount =1 WHERE selectedType =:type")
    void updateRetryCount(String  type);

    @Query("DELETE FROM ValidateId_IdentityType")
    void deleteAll();
    @Query("SELECT count(*) FROM ValidateId_IdentityType")
    int deletedCount();

    @Query("SELECT retryCount FROM ValidateId_IdentityType WHERE selectedType =:type")
    int getRetryCount(String  type);

  /*  @Query("SELECT count(*) FROM ValidateId_IdentityType WHERE deletedFlag = 1")
    int deletedCount();*/
}
