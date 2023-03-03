package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
@Entity
public class AddLicense {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int a_id;

    @ColumnInfo(name = "licenseNum")
    private String licenseNum;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "addressOne")
    private String addressOne;

    @ColumnInfo(name = "licenseState")
    private String licenseState;

    @ColumnInfo(name = "city")
    private String city;

    @ColumnInfo(name = "state")
    private String state;

    @ColumnInfo(name = "zip")
    private String zip;

    @ColumnInfo(name = "phone")
    private String phone;

    @ColumnInfo(name = "expiryDate")
    private String expiryDate;

    @Ignore
    public AddLicense(){

    }
    public AddLicense(String licenseNum, String licenseState) {
        this.licenseNum = licenseNum;
        this.licenseState = licenseState;
    }

    public int getA_id() {
        return a_id;
    }

    public void setA_id(int a_id) {
        this.a_id = a_id;
    }

    public String getLicenseNum() {
        return licenseNum;
    }

    public void setLicenseNum(String licenseNum) {
        this.licenseNum = licenseNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLicenseState() {
        return licenseState;
    }

    public void setLicenseState(String licenseState) {
        this.licenseState = licenseState;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddressOne() {
        return addressOne;
    }

    public void setAddressOne(String addressOne) {
        this.addressOne = addressOne;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }


}
