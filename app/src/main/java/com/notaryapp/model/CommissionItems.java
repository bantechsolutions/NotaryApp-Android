package com.notaryapp.model;

public class CommissionItems {
    String commission_state_name;
    String commission_number;
    String commission_status;

    public CommissionItems(String commission_state_name, String commission_number, String commission_status) {
        this.commission_state_name = commission_state_name;
        this.commission_number = commission_number;
        this.commission_status = commission_status;
    }

    public String getCommission_state_name() {
        return commission_state_name;
    }

    public void setCommission_state_name(String commission_state_name) {
        this.commission_state_name = commission_state_name;
    }

    public String getCommission_number() {
        return commission_number;
    }

    public void setCommission_number(String commission_number) {
        this.commission_number = commission_number;
    }

    public String getCommission_status() {
        return commission_status;
    }

    public void setCommission_status(String commission_status) {
        this.commission_status = commission_status;
    }
}
