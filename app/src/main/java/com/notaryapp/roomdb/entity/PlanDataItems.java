package com.notaryapp.roomdb.entity;


public class PlanDataItems {

    private String planName;
    private String planDesc;

    public PlanDataItems(String name, String desc) {
        this.planName = name;
        this.planDesc = desc;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanDesc() {
        return planDesc;
    }

    public void setPlanDesc(String planDesc) {
        this.planDesc = planDesc;
    }
}
