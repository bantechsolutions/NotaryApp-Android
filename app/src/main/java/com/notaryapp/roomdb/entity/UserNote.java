package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class UserNote implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "noteHeading")
    private String noteHeading;

    @ColumnInfo(name = "noteDetail")
    private String noteDetail;

    @Ignore
    public UserNote(){

    }
    public UserNote(int id, String noteHeading, String noteDetail) {
        this.id = id;
        this.noteHeading = noteHeading;
        this.noteDetail = noteDetail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNoteHeading() {
        return noteHeading;
    }

    public void setNoteHeading(String noteHeading) {
        this.noteHeading = noteHeading;
    }

    public String getNoteDetail() {
        return noteDetail;
    }

    public void setNoteDetail(String noteDetail) {
        this.noteDetail = noteDetail;
    }
}
