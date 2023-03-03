package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.AllTransactions;

import java.util.List;

@Dao
public interface AllOrdersDao {

    @Query("SELECT * FROM AllOrders")
    List<AllTransactions> getAllOrders();

}
