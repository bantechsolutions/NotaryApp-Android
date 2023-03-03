package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.Transactions;

import java.util.List;

@Dao
public interface TransactionsDao {

    @Query("SELECT transactionId FROM Transactions ORDER BY id DESC LIMIT 1 ")
    String getTransactionId();

    @Query("SELECT transactionKey FROM Transactions ORDER BY id DESC LIMIT 1 ")
    String getTransactionKey();

    @Query("SELECT * FROM Transactions ORDER BY id DESC")
    List<Transactions> getTransactions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Transactions transactions);

    @Delete
    void delete(Transactions transactions);

    @Query("SELECT count(*) from Transactions")
    int getCount();

    @Query("DELETE from Transactions")
    int deleteAll();

    @Query("UPDATE Transactions SET transactionId =:transactionId ,transactionKey =:transactionKey")
    void update(String transactionId,String transactionKey);
}
