package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Transactions implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "transactionId")
    private String transactionId;

    @ColumnInfo(name = "transactionKey")
    private String transactionKey;

    @ColumnInfo(name = "transactionDate")
    private String transactionDate;

    @ColumnInfo(name = "success")
    private boolean success;

    @Ignore
    public Transactions(){
    }

    public Transactions(String transactionId, String transactionKey, boolean success) {
        this.transactionId = transactionId;
        this.transactionKey = transactionKey;
        this.success = success;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionKey() {
        return transactionKey;
    }

    public void setTransactionKey(String transactionKey) {
        this.transactionKey = transactionKey;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }
}
