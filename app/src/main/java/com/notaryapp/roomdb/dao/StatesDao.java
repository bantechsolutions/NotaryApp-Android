package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.States;

import java.util.List;

@Dao
public interface StatesDao {
    @Query("SELECT * FROM states")
    States getStates();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(States states);

    @Query("SELECT count(*) FROM states")
    int getcount();

    @Query("SELECT stateName FROM states")
    List<String> getStateName();

    @Query("SELECT stateName FROM states WHERE stateCode=:sCode")
    String getStateNameFrmLocalDb (String sCode);

    @Query("SELECT stateCode FROM states WHERE stateName=:state")
    String getStateCode(String state);
}
