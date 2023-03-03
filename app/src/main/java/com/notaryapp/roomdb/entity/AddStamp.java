package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class AddStamp {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "license_num")
    public String license_num;

    @ColumnInfo(name = "stampImgPath")
    private String stampImgPath;

    @ColumnInfo(name = "stampImgName")
    private String stampImgName;

    @ColumnInfo(name = "stampPosition")
    private int stampPosition;

    @ColumnInfo(name = "licenseState")
    private String  licenseState;

    @Ignore
    public AddStamp() {
    }

    public AddStamp(String license_num, String stampImgPath,String licenseState) {
        this.license_num = license_num;
        this.stampImgPath = stampImgPath;
        this.licenseState = licenseState;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLicense_num() {
        return license_num;
    }

    public void setLicense_num(String license_num) {
        this.license_num = license_num;
    }

    public String getStampImgPath() {
        return stampImgPath;
    }

    public void setStampImgPath(String stampImgPath) {
        this.stampImgPath = stampImgPath;
    }

    public String getStampImgName() {
        return stampImgName;
    }

    public void setStampImgName(String stampImgName) {
        this.stampImgName = stampImgName;
    }

    public int getStampPosition() {
        return stampPosition;
    }

    public void setStampPosition(int stampPosition) {
        this.stampPosition = stampPosition;
    }

    public String getLicenseState() {
        return licenseState;
    }

    public void setLicenseState(String licenseState) {
        this.licenseState = licenseState;
    }
}
