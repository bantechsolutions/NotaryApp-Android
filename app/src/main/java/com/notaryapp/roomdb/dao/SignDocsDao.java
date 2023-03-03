package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.SignDocs;

@Dao
public interface SignDocsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SignDocs signDocs);

    @Delete
    void delete(SignDocs signDocs);

    @Query("SELECT count(*) from SignDocs")
    int getCount();

    @Query("UPDATE SignDocs SET email =:email ,success =:success")
    void updateUser(String email,boolean success);

    @Query("DELETE FROM SignDocs")
    void deleteAll();
}
