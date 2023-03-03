package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.Witness_IdentityType;

import java.util.List;

@Dao
public interface Witness_SelectIDDao {

    @Insert
    void insert(Witness_IdentityType identityType);

    @Delete
    void delete(Witness_IdentityType identityType);

    @Query("SELECT selectedType FROM Witness_IdentityType ORDER BY id DESC LIMIT 1")
    String getSelectedType();

    @Query("SELECT selectedType FROM Witness_IdentityType")
    List<String> getAllSelectedIds();

    @Query("UPDATE Witness_IdentityType SET retryCount =1 WHERE selectedType =:type")
    void updateRetryCount(String  type);

    @Query("DELETE FROM Witness_IdentityType")
    void deleteAll();
    @Query("SELECT count(*) FROM Witness_IdentityType")
    int deletedCount();

    @Query("SELECT retryCount FROM Witness_IdentityType WHERE selectedType =:type")
    int getRetryCount(String  type);
}
