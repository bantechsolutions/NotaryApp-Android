package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.ProfilePicture;

@Dao
public interface ProfilePictureDao {

    @Insert
    void insert(ProfilePicture profilePicture);

    @Delete
    void delete(ProfilePicture identityType);

    @Query("SELECT profilePic FROM ProfilePicture ")
    String getProfilePic();


    @Query("DELETE FROM ProfilePicture")
    void deleteAll();

    @Query("SELECT count(*) FROM ProfilePicture")
    int deletedCount();

}
