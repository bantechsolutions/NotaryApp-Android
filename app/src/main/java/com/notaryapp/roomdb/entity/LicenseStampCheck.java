package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class LicenseStampCheck {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "license_num")
    public String license_num;

    @ColumnInfo(name = "stampchk")
    public boolean stampChk;

    @ColumnInfo(name = "licenseState")
    public String licenseState;

    public LicenseStampCheck(String license_num,String licenseState, boolean stampChk) {
        this.license_num = license_num;
        this.licenseState = licenseState;
        this.stampChk = stampChk;
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

    public boolean isStampChk() {
        return stampChk;
    }

    public void setStampChk(boolean stampChk) {
        this.stampChk = stampChk;
    }

    public String getLicenseState() {
        return licenseState;
    }

    public void setLicenseState(String licenseState) {
        this.licenseState = licenseState;
    }
}
