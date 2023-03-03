package com.notaryapp.model;

import java.io.Serializable;

public class ReportItems implements Serializable {


    String report_id;
    String last_renewal_date;
    String full_address;
    String street;
    String unit_no;
    String city;
    String state;
    String zip;
    String user_name;
    String user_phone;
    String user_email;

    String created_date;
    String plan_title;
    String plan_type;
    String plan_status;
    String plan_updated_on;
    String plan_start_date;
    String plan_end_date;
    String plan_price;
    String total_count;

    /*public String getTotal_count() {
        return total_count;
    }

    public void setTotal_count(String total_count) {
        this.total_count = total_count;
    }*/


    public String getPlan_price() {
        return plan_price;
    }

    public void setPlan_price(String plan_price) {
        this.plan_price = plan_price;
    }

    public String getPlan_title() {
        return plan_title;
    }

    public void setPlan_title(String plan_title) {
        this.plan_title = plan_title;
    }

    public String getPlan_type() {
        return plan_type;
    }

    public void setPlan_type(String plan_type) {
        this.plan_type = plan_type;
    }

    public String getPlan_status() {
        return plan_status;
    }

    public void setPlan_status(String plan_status) {
        this.plan_status = plan_status;
    }

    public String getPlan_updated_on() {
        return plan_updated_on;
    }

    public void setPlan_updated_on(String plan_updated_on) {
        this.plan_updated_on = plan_updated_on;
    }

    public String getPlan_start_date() {
        return plan_start_date;
    }

    public void setPlan_start_date(String plan_start_date) {
        this.plan_start_date = plan_start_date;
    }

    public String getPlan_end_date() {
        return plan_end_date;
    }

    public void setPlan_end_date(String plan_end_date) {
        this.plan_end_date = plan_end_date;
    }



    public String getReport_id() {
        return report_id;
    }

    public void setReport_id(String report_id) {
        this.report_id = report_id;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getLast_renewal_date() {
        return last_renewal_date;
    }

    public void setLast_renewal_date(String last_renewal_date) {
        this.last_renewal_date = last_renewal_date;
    }

    public String getFull_address() {
        return full_address;
    }

    public void setFull_address(String full_address) {
        this.full_address = full_address;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getUnit_no() {
        return unit_no;
    }

    public void setUnit_no(String unit_no) {
        this.unit_no = unit_no;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }
}
