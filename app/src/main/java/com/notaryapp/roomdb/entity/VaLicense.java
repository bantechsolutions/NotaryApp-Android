package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class VaLicense {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "license")
    public String license;

    @ColumnInfo(name = "selectedLice")
    private boolean selectedLice;

    @ColumnInfo(name = "selectedImgPath")
    public String selectedImgPath;

    public VaLicense(String license, boolean selectedLice,String selectedImgPath) {
        this.license = license;
        this.selectedLice = selectedLice;
        this.selectedImgPath = selectedImgPath;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public boolean isSelectedLice() {
        return selectedLice;
    }

    public void setSelectedLice(boolean selectedLice) {
        this.selectedLice = selectedLice;
    }

    public String getSelectedImgPath() {
        return selectedImgPath;
    }

    public void setSelectedImgPath(String selectedImgPath) {
        this.selectedImgPath = selectedImgPath;
    }
}
