package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.notaryapp.roomdb.entity.WitnessReg;

import java.util.List;

@Dao
public interface WitnessRegDao {

    @Query("SELECT * FROM WitnessReg")
    List<WitnessReg> getWitness();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WitnessReg witnessReg);

    @Delete
    void delete(WitnessReg witnessReg);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateUser(WitnessReg witnessReg);

    @Query("SELECT email FROM WitnessReg")
    String getEmail();

    @Query("SELECT firstName FROM WitnessReg")
    String getName();

    @Query("SELECT count(*) from WitnessReg")
    int getCount();

    @Query("SELECT count(*) from WitnessReg")
    int getWitnessCount();

    @Query("UPDATE WitnessReg SET firstname =:firstName,lastname =:lName, phoneNo =:phn WHERE email =:email")
    int updateData(String email, String firstName, String lName, String phn);

    @Query("UPDATE WitnessReg SET email =:email")
    void update(String email);

    @Query("DELETE from WitnessReg")
    int deleteAll();
}
