package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.notaryapp.roomdb.entity.AllTransactions;

import java.util.List;

@Dao
public interface AllTransactionsDao {

    @Query("SELECT * FROM AllTransactions WHERE star = 0 ORDER BY doj DESC")
    List<AllTransactions> getAllTrans();

    @Query("SELECT * FROM AllTransactions WHERE star = 0 ORDER BY doj ASC")
    List<AllTransactions> getAllTransAsc();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AllTransactions allTransactions);

    @Query("DELETE  FROM AllTransactions")
    void delete();

    @Query("SELECT count(*) from AllTransactions")
    int getCount();

    @Query("SELECT * FROM AllTransactions WHERE tranType='Notary-App™' AND star = 0 ORDER BY doj DESC")
    List<AllTransactions> getAllVATrans();

    @Query("SELECT * FROM AllTransactions WHERE tranType='Notary-App™' AND star = 0 ORDER BY doj ASC")
    List<AllTransactions> getAllVATransAsc();

    @Query("SELECT * FROM AllTransactions WHERE tranType='Verify ID' AND star = 0 ORDER BY doj DESC")
    List<AllTransactions> getAllVIDTrans();

    @Query("SELECT * FROM AllTransactions WHERE tranType='Verify ID' AND star = 0 ORDER BY doj ASC")
    List<AllTransactions> getAllVIDTransAsc();

    @Query("SELECT * FROM AllTransactions WHERE (signerName LIKE :query AND star = 0) OR (sealCode LIKE :query AND star = 0) OR (tranType =:query AND star = 0) OR (docuType LIKE :query AND star = 0) OR (transcomp LIKE :query AND star = 0) OR (tranId LIKE :query AND star = 0) ORDER BY id DESC")
    List<AllTransactions> getSearchDesc(String query);

    @Query("SELECT * FROM AllTransactions WHERE (tranType='Verify ID' AND signerName LIKE :query AND star = 0) OR (tranType='Verify ID' AND sealCode LIKE :query AND star = 0) OR (tranType='Verify ID' AND docuType LIKE :query AND star = 0) OR (tranType='Verify ID' AND transcomp LIKE :query AND star = 0) OR (tranType='Verify ID' AND tranId LIKE :query AND star = 0) ORDER BY id DESC")
    List<AllTransactions> getVerifySearch(String query);

    @Query("SELECT * FROM AllTransactions WHERE (tranType='Notary-App™' AND signerName LIKE :query AND star = 0) OR (tranType='Notary-App™' AND sealCode LIKE :query AND star = 0) OR (tranType='Notary-App™' AND docuType LIKE :query AND star = 0) OR (tranType='Notary-App™' AND transcomp LIKE :query AND star = 0) OR (tranType='Notary-App™' AND tranId LIKE :query AND star = 0) ORDER BY id DESC")
    List<AllTransactions> getVASearch(String query);

    @Query("SELECT * FROM AllTransactions WHERE (star = 1 AND signerName LIKE :query) OR (star = 1 AND sealCode LIKE :query) OR (star = 1 AND tranType LIKE :query) OR (star = 1 AND docuType LIKE :query) OR (star = 1 AND transcomp LIKE :query) OR (star = 1 AND tranId LIKE :query) ORDER BY id DESC")
    List<AllTransactions> getStarSearch(String query);

    @Query("SELECT * FROM AllTransactions WHERE star = 1")
    List<AllTransactions> getAllStar();

    @Query("UPDATE AllTransactions SET star =1 WHERE tranId =:id")
    void updateStar(String  id);
}
