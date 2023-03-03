package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class WitnessReg implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "firstName")
    private String firstName;

    @ColumnInfo(name = "lastName")
    private String lastName;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "phoneNo")
    private String phoneNo;

    @ColumnInfo(name = "addressOne")
    private String addressOne;

    @ColumnInfo(name = "addressTwo")
    private String addressTwo;

    @ColumnInfo(name = "cityName")
    private String cityName;

    @ColumnInfo(name = "stateName")
    private String stateName;

    @ColumnInfo(name = "zipCode")
    private String zipCode;

    @ColumnInfo(name = "signerEmail")
    private String signerEmail;

    @ColumnInfo(name = "scanRef")
    private String scanRef;

    @ColumnInfo(name = "proImagePath")
    private String proImagePath;

    @Ignore
    public WitnessReg(){

    }

    public WitnessReg(String firstName, String lastName,
                      String email, String phoneNo,String scanRef, String signerEmail,String proImagePath,
                      String city,String state, String zip, String add1, String add2) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.scanRef = scanRef;
        this.signerEmail = signerEmail;
        this.proImagePath = proImagePath;

        this.cityName = city;
        this.stateName = state;
        this.zipCode = zip;
        this.addressOne = add1;
        this.addressTwo = add2;
    }

    public WitnessReg(String firstName, String lastName,
                      String email, String phoneNo,String scanRef, String signerEmail,String proImagePath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.scanRef = scanRef;
        this.signerEmail = signerEmail;
        this.proImagePath = proImagePath;
    }

    public String getAddressOne() {
        return addressOne;
    }

    public void setAddressOne(String addressOne) {
        this.addressOne = addressOne;
    }

    public String getAddressTwo() {
        return addressTwo;
    }

    public void setAddressTwo(String addressTwo) {
        this.addressTwo = addressTwo;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getSignerEmail() {
        return signerEmail;
    }

    public void setSignerEmail(String signerEmail) {
        this.signerEmail = signerEmail;
    }
    public String getScanRef() {
        return scanRef;
    }

    public void setScanRef(String scanRef) {
        this.scanRef = scanRef;
    }

    public String getProImagePath() {
        return proImagePath;
    }

    public void setProImagePath(String proImagePath) {
        this.proImagePath = proImagePath;
    }
}
