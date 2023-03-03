package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.notaryapp.roomdb.entity.UserLocation;

@Dao
public interface UserLocationDao {

    @Query("SELECT * FROM UserLocation")
    UserLocation getLocation();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserLocation userLocation);

    @Delete
    void delete(UserLocation userLocation);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateUser(UserLocation userLocation);

    @Query("SELECT COUNT(*) FROM UserLocation")
    int getLocationCount();

    @Query("DELETE FROM UserLocation")
    void deleteAll();

    @Query("UPDATE UserLocation SET streetName =:streetName ,cityName =:cityName,stateName =:stateName, pinCode =:pinCode WHERE id =:id")
    int update(String streetName, String cityName, String stateName, String pinCode, int id);

}
