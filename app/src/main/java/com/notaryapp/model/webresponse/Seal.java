
package com.notaryapp.model.webresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Seal {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("notary")
    @Expose
    private String notary;
    @SerializedName("tranid")
    @Expose
    private String tranid;
    @SerializedName("signer")
    @Expose
    private String signer;
    @SerializedName("sealCode")
    @Expose
    private String sealCode;
    @SerializedName("sealUrl")
    @Expose
    private String sealUrl;
    @SerializedName("license")
    @Expose
    private String license;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("mdfddt")
    @Expose
    private String mdfddt;
    @SerializedName("licenseState")
    @Expose
    private String licenseState;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNotary() {
        return notary;
    }

    public void setNotary(String notary) {
        this.notary = notary;
    }

    public String getTranid() {
        return tranid;
    }

    public void setTranid(String tranid) {
        this.tranid = tranid;
    }

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public String getSealCode() {
        return sealCode;
    }

    public void setSealCode(String sealCode) {
        this.sealCode = sealCode;
    }

    public String getSealUrl() {
        return sealUrl;
    }

    public void setSealUrl(String sealUrl) {
        this.sealUrl = sealUrl;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getMdfddt() {
        return mdfddt;
    }

    public void setMdfddt(String mdfddt) {
        this.mdfddt = mdfddt;
    }

    public String getLicenseState() {
        return licenseState;
    }

    public void setLicenseState(String licenseState) {
        this.licenseState = licenseState;
    }

}
