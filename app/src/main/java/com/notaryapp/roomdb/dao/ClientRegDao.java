package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.notaryapp.roomdb.entity.ClientReg;

@Dao
public interface ClientRegDao {

    @Query("SELECT * FROM ClientReg")
    ClientReg getUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ClientReg clientReg);

    @Delete
    void delete(ClientReg clientReg);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateClient(ClientReg clientReg);

    @Query("SELECT email FROM clientReg")
    String getEmail();

    @Query("SELECT firstName FROM ClientReg")
    String getName();

    @Query("SELECT * FROM ClientReg ORDER BY id DESC LIMIT 1")
    ClientReg getClientData();

    @Query("SELECT count(*) from ClientReg")
    int getCount();

    @Query("UPDATE ClientReg SET email =:email ,firstname =:firstName,lastname =:lName, phoneNo =:phn WHERE id =:id")
    int update(String email,String firstName,String lName,String phn,int id);
}
