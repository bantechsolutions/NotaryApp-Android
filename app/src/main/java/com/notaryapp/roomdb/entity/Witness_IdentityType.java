package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Witness_IdentityType {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "selectedType")
    private String selectedType;

    @ColumnInfo(name = "deletedFlag")
    private boolean deletedFlag;
    @ColumnInfo(name = "retryCount")
    private int retryCount;

    public Witness_IdentityType(String selectedType, boolean deletedFlag, int retryCount) {
        this.selectedType = selectedType;
        this.deletedFlag = deletedFlag;
        this.retryCount = retryCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(String selectedType) {
        this.selectedType = selectedType;
    }

    public boolean isDeletedFlag() {
        return deletedFlag;
    }

    public void setDeletedFlag(boolean deletedFlag) {
        this.deletedFlag = deletedFlag;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
