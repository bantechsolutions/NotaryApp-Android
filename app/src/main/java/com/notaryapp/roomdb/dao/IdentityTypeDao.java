package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.IdentityType;

import java.util.List;

@Dao
public interface IdentityTypeDao {

    @Insert
    void insert(IdentityType identityType);

    @Delete
    void delete(IdentityType identityType);
    @Query("SELECT selectedType FROM IdentityType ORDER BY id DESC LIMIT 1")
    String getSelectedType();

    @Query("SELECT selectedType FROM IdentityType")
    List<String> getAllSelectedIds();

    @Query("UPDATE IdentityType SET retryCount =1 WHERE selectedType =:type")
    void updateRetryCount(String  type);

    @Query("DELETE FROM IdentityType")
    void deleteAll();

    @Query("SELECT count(*) FROM IdentityType")
    int deletedCount();

    @Query("SELECT retryCount FROM IdentityType WHERE selectedType =:type")
    int getRetryCount(String  type);

  /*  @Insert
    void insert(IdentityType identityType);

    @Query("SELECT * FROM IdentityType WHERE deletedFlag = 0")
    IdentityType getInfo();

    @Query("SELECT selectedType FROM IdentityType WHERE deletedFlag = 0")
    List<String> getDeletedType();

    @Query("UPDATE IdentityType SET typeId =:id")
    void update(int id);

    @Query("UPDATE IdentityType SET deletedFlag =1 WHERE selectedType =:type")
    void updateDelete(String  type);

    @Query("DELETE FROM IdentityType")
    void deleteAll();*/
}
