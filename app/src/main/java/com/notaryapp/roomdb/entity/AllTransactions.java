package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class AllTransactions {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "signerName")
    public String signerName;

    @ColumnInfo(name = "sealCode")
    public String sealCode;

    @ColumnInfo(name = "tranType")
    public String tranType;

    @ColumnInfo(name = "doj")
    private String doj;

    @ColumnInfo(name = "docuType")
    public String docuType;

    public String getSignerCount() {
        return signerCount;
    }

    public void setSignerCount(String signerCount) {
        this.signerCount = signerCount;
    }

    @ColumnInfo(name = "transcomp")
    private String transcomp;

    @ColumnInfo(name = "tranId")
    private String tranId;

    @ColumnInfo(name = "signerCount")
    public String signerCount;

    @ColumnInfo(name = "photo")
    public String photo;

    @ColumnInfo(name = "star")
    private boolean star;

    public AllTransactions(String signerName,
                           String sealCode,
                           String tranType,
                           String doj,
                           String docuType,
                           String transcomp,String tranId, boolean star, String photo, String signerCount) {
        this.signerName = signerName;
        this.sealCode = sealCode;
        this.tranType = tranType;
        this.doj = doj;
        this.docuType = docuType;
        this.transcomp = transcomp;
        this.star = star;
        this.tranId = tranId;
        this.photo = photo;
        this.signerCount = signerCount;
    }

    public String getSignerName() {
        return signerName;
    }

    public void setSignerName(String signerName) {
        this.signerName = signerName;
    }

    public String getSealCode() {
        return sealCode;
    }

    public void setSealCode(String sealCode) {
        this.sealCode = sealCode;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isStar() {
        return star;
    }

    public void setStar(boolean star) {
        this.star = star;
    }

    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
    }

    public String getDoj() {
        return doj;
    }

    public void setDoj(String doj) {
        this.doj = doj;
    }

    public String getDocuType() {
        return docuType;
    }

    public void setDocuType(String docuType) {
        this.docuType = docuType;
    }

    public String getTranscomp() {
        return transcomp;
    }

    public void setTranscomp(String transcomp) {
        this.transcomp = transcomp;
    }

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }
}
