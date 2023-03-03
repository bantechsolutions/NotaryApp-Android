package com.notaryapp.model;

public class MembershipPackagePlans {

    private String planName;
    private String benefits;
    private String transLimit;
    private String singleFee;
    private String cost;
    private String planType;
    private String monthlyFee;
    private String category;

    public MembershipPackagePlans(String planName, String benefits, String transLimit, String singleFee,
    String cost, String planType,String monthlyFee,String category) {
        this.planName = planName;
        this.benefits = benefits;
        this.transLimit = transLimit;
        this.singleFee = singleFee;
        this.cost = cost;
        this.planType = planType;
        this.monthlyFee = monthlyFee;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getTransLimit() {
        return transLimit;
    }

    public void setTransLimit(String transLimit) {
        this.transLimit = transLimit;
    }

    public String getSingleFee() {
        return singleFee;
    }

    public void setSingleFee(String singleFee) {
        this.singleFee = singleFee;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(String monthlyFee) {
        this.monthlyFee = monthlyFee;
    }
}
