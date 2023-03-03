package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class  SignerReg implements Serializable {

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

    @ColumnInfo(name = "proImagePath")
    private String proImagePath;

    @ColumnInfo(name = "scanReference")
    private String scanReference;

    @ColumnInfo(name = "signerType")
    private String signerType;

    @ColumnInfo(name = "witness")
    private boolean witness;

    @ColumnInfo(name = "signatureImagePath")
    private String signatureImagePath;

    @ColumnInfo(name = "thumbImagePath")
    private String thumbImagePath;

    @Ignore
    public SignerReg(){

    }
    public SignerReg(String firstName, String lastName, String email, String phoneNo,
                     String scanReference,String proImagePath, String signerType,boolean witness) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.proImagePath = proImagePath;
        this.scanReference = scanReference;
        this.signerType = signerType;
        this.witness = witness;
    }

    public SignerReg(String firstName, String lastName, String email, String phoneNo,
                     String scanReference,String proImagePath, String signerType,boolean witness,
                     String city,String state, String zip, String add1, String add2) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.proImagePath = proImagePath;
        this.scanReference = scanReference;
        this.signerType = signerType;
        this.witness = witness;

        this.cityName = city;
        this.stateName = state;
        this.zipCode = zip;
        this.addressOne = add1;
        this.addressTwo = add2;
    }

    public SignerReg(String firstName, String lastName, String email, String phoneNo,
                     String scanReference,String proImagePath, String signerType,boolean witness,
                     String city,String state, String zip, String add1, String add2, String signatureImagePath,
                     String thumbImagePath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.proImagePath = proImagePath;
        this.scanReference = scanReference;
        this.signerType = signerType;
        this.witness = witness;

        this.cityName = city;
        this.stateName = state;
        this.zipCode = zip;
        this.addressOne = add1;
        this.addressTwo = add2;
        this.signatureImagePath = signatureImagePath;
        this.thumbImagePath = thumbImagePath;
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

    public String getProImagePath() {
        return proImagePath;
    }

    public void setProImagePath(String proImagePath) {
        this.proImagePath = proImagePath;
    }

    public String getScanReference() {
        return scanReference;
    }

    public void setScanReference(String scanReference) {
        this.scanReference = scanReference;
    }

    public boolean isWitness() {
        return witness;
    }

    public void setWitness(boolean witness) {
        this.witness = witness;
    }

    public String getSignerType() {
        return signerType;
    }

    public void setSignerType(String signerType) {
        this.signerType = signerType;
    }

    public String getSignatureImagePath() {
        return signatureImagePath;
    }

    public void setSignatureImagePath(String signatureImagePath) {
        this.signatureImagePath = signatureImagePath;
    }

    public String getThumbImagePath() {
        return thumbImagePath;
    }

    public void setThumbImagePath(String thumbImagePath) {
        this.thumbImagePath = thumbImagePath;
    }
}
