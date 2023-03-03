package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.NotaryApp_IdentityType;

import java.util.List;

@Dao
public interface NotaryApp_SelectIDDao {

    @Insert
    void insert(NotaryApp_IdentityType identityType);

    @Delete
    void delete(NotaryApp_IdentityType identityType);

     /*@Query("UPDATE ValidateId_IdentityType SET typeId = :identityType , selectedType = :selectIdType WHERE deletedFlag= 0")
      void update(int identityType,String selectIdType);

    @Query("SELECT selectedType FROM ValidateId_IdentityType WHERE deletedFlag =0")
    String getSelectIdType();*/

    @Query("SELECT selectedType FROM NotaryApp_IdentityType ORDER BY id DESC LIMIT 1")
    String getSelectedType();

    @Query("SELECT selectedType FROM NotaryApp_IdentityType")
    List<String> getAllSelectedIds();

    @Query("UPDATE NotaryApp_IdentityType SET retryCount =1 WHERE selectedType =:type")
    void updateRetryCount(String type);

    @Query("DELETE FROM NotaryApp_IdentityType")
    void deleteAll();

    @Query("SELECT count(*) FROM NotaryApp_IdentityType")
    int deletedCount();

    @Query("SELECT retryCount FROM NotaryApp_IdentityType WHERE selectedType =:type")
    int getRetryCount(String  type);
}
