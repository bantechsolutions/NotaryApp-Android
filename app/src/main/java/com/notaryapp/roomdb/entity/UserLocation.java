package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class UserLocation implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "streetName")
    private String streetName;

    @ColumnInfo(name = "cityName")
    private String cityName;

    @ColumnInfo(name = "stateName")
    private String stateName;

    @ColumnInfo(name = "pinCode")
    private String pinCode;

    @ColumnInfo(name = "success")
    private boolean success;

    @Ignore
    public UserLocation(){

    }
    public UserLocation(int id, String streetName, String cityName,
                        String stateName, String pinCode, boolean success) {
        this.id = id;
        this.streetName = streetName;
        this.cityName = cityName;
        this.stateName = stateName;
        this.pinCode = pinCode;
        this.success = success;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
