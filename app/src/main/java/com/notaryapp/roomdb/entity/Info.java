package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Info {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "selectId")
    private String selectId;

    @ColumnInfo(name = "profile")
    public String profile;

    @ColumnInfo(name = "license")
    private String license;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "govtId")
    public String govtId;

    @ColumnInfo(name = "docLicense")
    private String docLicense;

    @ColumnInfo(name = "stamp")
    private String stamp;

    @ColumnInfo(name = "captureStamp")
    private String captureStamp;

    @ColumnInfo(name = "mulLicense")
    public String mulLicense;

    @ColumnInfo(name = "alternateId")
    private String alternateId;


    @ColumnInfo(name = "profileLicenses")
    private String profileLicenses;

    @ColumnInfo(name = "verifyID")
    public String verifyID;

    @ColumnInfo(name = "vaSigners ")
    private String vaSigners ;

    @ColumnInfo(name = "vaIDtype")
    private String vaIDtype;

    @ColumnInfo(name = "vaSigndocuments")
    private String vaSigndocuments;

    @ColumnInfo(name = "witnessIDtype")
    public String witnessIDtype;

    @ColumnInfo(name = "vaAddseal")
    private String vaAddseal;

    @ColumnInfo(name = "vaAdddocuments")
    private String vaAdddocuments;

    @ColumnInfo(name = "vaAddLocation")
    private String vaAddLocation;

    @ColumnInfo(name = "apnNumber")
    public String apnNumber;

    @ColumnInfo(name = "confirmLocation")
    private String confirmLocation;

    @ColumnInfo(name = "notaryLicense")
    private String notaryLicense;

    public String getGovtId() {
        return govtId;
    }

    public void setGovtId(String govtId) {
        this.govtId = govtId;
    }

    public String getNotaryLicense() {
        return notaryLicense;
    }

    public void setNotaryLicense(String notaryLicense) {
        this.notaryLicense = notaryLicense;
    }

    public String getStamp() {
        return stamp;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
    }

    public String getCaptureStamp() {
        return captureStamp;
    }

    public void setCaptureStamp(String captureStamp) {
        this.captureStamp = captureStamp;
    }

    public String getMulLicense() {
        return mulLicense;
    }

    public void setMulLicense(String mulLicense) {
        this.mulLicense = mulLicense;
    }

    public String getAlternateId() {
        return alternateId;
    }

    public void setAlternateId(String alternateId) {
        this.alternateId = alternateId;
    }

    public Info(String password, String selectId, String profile, String license, String address,
                String govtId, String docLicense, String stamp, String captureStamp,
                String mulLicense, String alternateId, String profileLicenses, String verifyID,
                String vaSigners, String vaIDtype, String vaSigndocuments, String witnessIDtype,
                String vaAddseal, String vaAdddocuments, String vaAddLocation, String apnNumber,
                String confirmLocation, String notaryLicense) {
        this.password = password;
        this.selectId = selectId;
        this.profile = profile;
        this.license = license;
        this.address = address;
        this.govtId = govtId;
        this.docLicense = docLicense;
        this.stamp = stamp;
        this.captureStamp = captureStamp;
        this.mulLicense = mulLicense;
        this.alternateId = alternateId;


        this.profileLicenses = profileLicenses;
        this.verifyID = verifyID;
        this.vaSigners = vaSigners;
        this.vaIDtype = vaIDtype;
        this.vaSigndocuments = vaSigndocuments;
        this.witnessIDtype = witnessIDtype;
        this.vaAddseal = vaAddseal;
        this.vaAdddocuments = vaAdddocuments;
        this.vaAddLocation = vaAddLocation;
        this.apnNumber = apnNumber;
        this.confirmLocation = confirmLocation;
        this.notaryLicense = notaryLicense;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSelectId() {
        return selectId;
    }

    public void setSelectId(String selectId) {
        this.selectId = selectId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDocLicense() {
        return docLicense;
    }

    public void setDocLicense(String docLicense) {
        this.docLicense = docLicense;
    }

    public String getProfileLicenses() {
        return profileLicenses;
    }

    public void setProfileLicenses(String profileLicenses) {
        this.profileLicenses = profileLicenses;
    }

    public String getVerifyID() {
        return verifyID;
    }

    public void setVerifyID(String verifyID) {
        this.verifyID = verifyID;
    }

    public String getVaSigners() {
        return vaSigners;
    }

    public void setVaSigners(String vaSigners) {
        this.vaSigners = vaSigners;
    }

    public String getVaIDtype() {
        return vaIDtype;
    }

    public void setVaIDtype(String vaIDtype) {
        this.vaIDtype = vaIDtype;
    }

    public String getVaSigndocuments() {
        return vaSigndocuments;
    }

    public void setVaSigndocuments(String vaSigndocuments) {
        this.vaSigndocuments = vaSigndocuments;
    }

    public String getWitnessIDtype() {
        return witnessIDtype;
    }

    public void setWitnessIDtype(String witnessIDtype) {
        this.witnessIDtype = witnessIDtype;
    }

    public String getVaAddseal() {
        return vaAddseal;
    }

    public void setVaAddseal(String vaAddseal) {
        this.vaAddseal = vaAddseal;
    }

    public String getVaAdddocuments() {
        return vaAdddocuments;
    }

    public void setVaAdddocuments(String vaAdddocuments) {
        this.vaAdddocuments = vaAdddocuments;
    }

    public String getVaAddLocation() {
        return vaAddLocation;
    }

    public void setVaAddLocation(String vaAddLocation) {
        this.vaAddLocation = vaAddLocation;
    }

    public String getApnNumber() {
        return apnNumber;
    }

    public void setApnNumber(String apnNumber) {
        this.apnNumber = apnNumber;
    }

    public String getConfirmLocation() {
        return confirmLocation;
    }

    public void setConfirmLocation(String confirmLocation) {
        this.confirmLocation = confirmLocation;
    }
}
