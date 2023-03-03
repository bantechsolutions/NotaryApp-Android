package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AllOrders {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "planName")
    public String planName;

    @ColumnInfo(name = "planType")
    public String planType;

    @ColumnInfo(name = "orderNo")
    public String orderNo;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "cost")
    public String cost;

    @ColumnInfo(name = "orderStatus")
    public String orderStatus;

    public AllOrders(String planName, String planType, String orderNo, String date, String cost, String orderStatus) {
        this.planName = planName;
        this.planType = planType;
        this.orderNo = orderNo;
        this.date = date;
        this.cost = cost;
        this.orderStatus = orderStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
