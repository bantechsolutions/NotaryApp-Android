package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.notaryapp.roomdb.entity.UserReg;

@Dao
public interface UserRegDao {

    @Query("SELECT * FROM UserReg")
    UserReg getUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserReg userReg);

    @Delete
    void delete(UserReg userReg);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateUser(UserReg userReg);

    @Query("SELECT appId FROM UserReg")
    String getAppId();

    @Query("SELECT email FROM UserReg")
    String getEmail();

    @Query("SELECT firstName FROM UserReg")
    String getName();

    @Query("SELECT lastName FROM UserReg")
    String getLastName();

    @Query("SELECT * FROM UserReg")
    UserReg getUserData();

    @Query("SELECT count(*) from UserReg")
    int getCount();

    @Query("UPDATE UserReg SET email =:email, appId = :appId, firstname =:firstName,lastname =:lName, phoneNo =:phn WHERE id =:id")
    int update(String email, String appId, String firstName,String lName,String phn,int id);

    @Query("UPDATE UserReg SET email =:email ,firstname =:firstName,lastname =:lastName")
    void update(String email,String firstName,String lastName);

    @Query("UPDATE UserReg SET appId =:appId")
    void updateAppID(String appId);
}
