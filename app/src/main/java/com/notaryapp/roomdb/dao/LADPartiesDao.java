package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.LADParties;

import java.util.List;

@Dao
public interface LADPartiesDao {
    @Query("SELECT * FROM LADParties")
    List<LADParties> getStates();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LADParties states);

    @Query("SELECT count(*) FROM LADParties")
    int getcount();

    @Query("SELECT stateName FROM LADParties")
    List<String> getStateName();

    @Query("SELECT stateName FROM LADParties WHERE cityName=:sCode")
    String getStateNameFrmLocalDb (String sCode);

    @Query("SELECT cityName FROM LADParties WHERE stateName=:state")
    String getStateCode(String state);

    @Query("DELETE from LADParties")
    int deleteAll();

    @Query("SELECT * FROM LADParties WHERE email =:email")
    LADParties getParties(String email);
}
