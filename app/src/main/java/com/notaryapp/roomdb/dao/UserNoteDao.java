package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.UserNote;

@Dao
public interface UserNoteDao {
    @Query("SELECT * FROM UserNote")
    UserNote getUserNote();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserNote userNote);

    @Delete
    void delete(UserNote userNote);

    @Query("SELECT count(*) from UserNote")
    int getCount();

    @Query("UPDATE UserNote SET noteHeading =:noteHeading ,noteDetail =:noteDetail")
    void updateUser(String noteHeading, String noteDetail);

    @Query("DELETE FROM UserNote")
    void deleteAll();
}
