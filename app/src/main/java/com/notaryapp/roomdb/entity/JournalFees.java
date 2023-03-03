package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class JournalFees implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "notarizationType")
    private String notarizationType;

    @ColumnInfo(name = "feeAmount")
    private String feeAmount;

    @ColumnInfo(name = "isCollected")
    private boolean isCollected;

    @Ignore
    public JournalFees(){

    }
    public JournalFees(int id, String notarizationType, String feeAmount, boolean isCollected) {
        this.id = id;
        this.notarizationType = notarizationType;
        this.feeAmount = feeAmount;
        this.isCollected = isCollected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotarizationType() {
        return notarizationType;
    }

    public void setNotarizationType(String notarizationType) {
        this.notarizationType = notarizationType;
    }

    public String getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(String feeAmount) {
        this.feeAmount = feeAmount;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }
}
