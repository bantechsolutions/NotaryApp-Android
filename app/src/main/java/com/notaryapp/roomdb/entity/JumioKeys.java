package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class JumioKeys {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "jumiokey")
    public String jumiokey;

    @ColumnInfo(name = "jumiosecret")
    private String jumiosecret;


    public JumioKeys(String jumiokey, String jumiosecret) {
        this.jumiokey = jumiokey;
        this.jumiosecret = jumiosecret;
    }

    public String getJumiokey() {
        return jumiokey;
    }

    public void setJumiokey(String jumiokey) {
        this.jumiokey = jumiokey;
    }

    public String getJumiosecret() {
        return jumiosecret;
    }

    public void setJumiosecret(String jumiosecret) {
        this.jumiosecret = jumiosecret;
    }

}
