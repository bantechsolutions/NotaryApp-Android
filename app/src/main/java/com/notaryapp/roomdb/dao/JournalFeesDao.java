package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.JournalFees;

@Dao
public interface JournalFeesDao {

    @Query("SELECT * FROM JournalFees")
    JournalFees getJournalFees();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(JournalFees journalFees);

    @Delete
    void delete(JournalFees journalFees);

    @Query("SELECT count(*) from JournalFees")
    int getCount();

    @Query("UPDATE JournalFees SET notarizationType =:notarizationType, feeAmount =:feeAmount, isCollected =:isCollected")
    void updateUser(String notarizationType, String feeAmount,boolean isCollected);

    @Query("DELETE FROM JournalFees")
    void deleteAll();
}
