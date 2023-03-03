package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class JumioScanDetails {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "scanRef")
    public String scanRef;

    @ColumnInfo(name = "firstName")
    private String firstName;

    @ColumnInfo(name = "lastName")
    private String lastName;

    @ColumnInfo(name = "docType")
    private String docType;

    public JumioScanDetails(String scanRef, String firstName, String lastName,String docType) {
        this.scanRef = scanRef;
        this.firstName = firstName;
        this.lastName = lastName;
        this.docType = docType;
    }

    public String getScanRef() {
        return scanRef;
    }

    public void setScanRef(String scanRef) {
        this.scanRef = scanRef;
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

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }
}
