package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class DocumentsModel implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "photoId")
    private String photoId;

    @ColumnInfo(name = "docName")
    private String docName;

    @ColumnInfo(name = "stampName")
    private String stampName;


    @Ignore
    public DocumentsModel(){
    }

    public DocumentsModel(String docName, String photoId, String stampName ) {
        this.docName = docName;
        this.photoId = photoId;
        this.stampName = stampName;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getStampName() {
        return stampName;
    }

    public void setStampName(String stampName) {
        this.stampName = stampName;
    }

}
