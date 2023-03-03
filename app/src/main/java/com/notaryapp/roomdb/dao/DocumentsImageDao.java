package com.notaryapp.roomdb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.notaryapp.roomdb.entity.DocumentsModel;

import java.util.List;

@Dao
public interface DocumentsImageDao {

    @Query("SELECT * FROM DocumentsModel")
    List<DocumentsModel> getDocs();

    @Query("SELECT * FROM DocumentsModel WHERE stampName =:stampName")
    List<DocumentsModel> getFilteredDocs(String stampName);

    @Query("SELECT * FROM DocumentsModel GROUP BY stampName")
    List<DocumentsModel> getDisDocs();

    @Query("SELECT COUNT(stampName) FROM DocumentsModel WHERE stampName =:stampName")
    int countDocs(String stampName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DocumentsModel documentsImageDao);

    @Delete
    void delete(DocumentsModel docPics);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateUser(DocumentsModel docPics);

    @Query("DELETE from DocumentsModel")
    void deleteAll();

    @Query("SELECT stampName from DocumentsModel WHERE photoId =:pId ORDER BY docName ASC")
    String getDocName(String pId);
}
