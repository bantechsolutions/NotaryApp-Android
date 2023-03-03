package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ValidateId_IdentityType {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "selectedType")
    private String selectedType;

    @ColumnInfo(name = "deleteFlag")
    private boolean deleteFlag;

    @ColumnInfo(name = "retryCount")
    private int retryCount;

    public ValidateId_IdentityType(String selectedType, boolean deleteFlag, int retryCount) {
        this.selectedType = selectedType;
        this.deleteFlag = deleteFlag;
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

    public boolean isDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
