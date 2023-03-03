package com.notaryapp.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class VACustomer {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name =  "cusId")
    private int cusId;

    @ColumnInfo(name =  "daysLeft")
    private int daysLeft;

    @ColumnInfo(name =  "transactionsLeft")
    private int transactionsLeft;

    @ColumnInfo(name =  "email")
    private String email;

    @ColumnInfo(name =  "stripe_customer_id")
    private String stripe_customer_id;

    @ColumnInfo(name =  "current_membership")
    private String current_membership;

    @ColumnInfo(name =  "total_bought")
    private int total_bought;

    @ColumnInfo(name =  "total_used")
    private int total_used;

    @ColumnInfo(name =  "created_at")
    private String created_at;

    @ColumnInfo(name =  "updated_at")
    private String updated_at;

    @ColumnInfo(name =  "started_at")
    private String started_at;

    @ColumnInfo(name =  "ending_at")
    private String ending_at;

    @ColumnInfo(name =  "current_date")
    private String current_date;

    @ColumnInfo(name =  "is_active")
    private int is_active;

    @ColumnInfo(name =  "stripe_active_subscription_id")
    private String stripe_active_subscription_id;

    public int getCusId() {
        return cusId;
    }

    public void setCusId(int cusId) {
        this.cusId = cusId;
    }

    public int getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(int daysLeft) {
        this.daysLeft = daysLeft;
    }

    public int getTransactionsLeft() {
        return transactionsLeft;
    }

    public void setTransactionsLeft(int transactionsLeft) {
        this.transactionsLeft = transactionsLeft;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStripe_customer_id() {
        return stripe_customer_id;
    }

    public void setStripe_customer_id(String stripe_customer_id) {
        this.stripe_customer_id = stripe_customer_id;
    }

    public String getCurrent_membership() {
        return current_membership;
    }

    public void setCurrent_membership(String current_membership) {
        this.current_membership = current_membership;
    }

    public int getTotal_bought() {
        return total_bought;
    }

    public void setTotal_bought(int total_bought) {
        this.total_bought = total_bought;
    }

    public int getTotal_used() {
        return total_used;
    }

    public void setTotal_used(int total_used) {
        this.total_used = total_used;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getStarted_at() {
        return started_at;
    }

    public void setStarted_at(String started_at) {
        this.started_at = started_at;
    }

    public String getEnding_at() {
        return ending_at;
    }

    public void setEnding_at(String ending_at) {
        this.ending_at = ending_at;
    }

    public String getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(String current_date) {
        this.current_date = current_date;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public String getStripe_active_subscription_id() {
        return stripe_active_subscription_id;
    }

    public void setStripe_active_subscription_id(String stripe_active_subscription_id) {
        this.stripe_active_subscription_id = stripe_active_subscription_id;
    }
}
