package com.notaryapp.model;

import java.io.Serializable;

public class OrderItems implements Serializable {

    String email;
    String id;
    String objecttype;
    String price;
    String created_on;
    String unit_purchased;
    String updated_on;
    String stripeobjectid;
    String status;

    String receipturl;
    String receiptpdf;
    String planname;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObjecttype() {
        return objecttype;
    }

    public void setObjecttype(String objecttype) {
        this.objecttype = objecttype;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getUnit_purchased() {
        return unit_purchased;
    }

    public void setUnit_purchased(String unit_purchased) {
        this.unit_purchased = unit_purchased;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }

    public String getStripeobjectid() {
        return stripeobjectid;
    }

    public void setStripeobjectid(String stripeobjectid) {
        this.stripeobjectid = stripeobjectid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceipturl() {
        return receipturl;
    }

    public void setReceipturl(String receipturl) {
        this.receipturl = receipturl;
    }

    public String getReceiptpdf() {
        return receiptpdf;
    }

    public void setReceiptpdf(String receiptpdf) {
        this.receiptpdf = receiptpdf;
    }

    public String getPlanname() {
        return planname;
    }

    public void setPlanname(String planname) {
        this.planname = planname;
    }
}
