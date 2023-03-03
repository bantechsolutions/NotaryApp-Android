
package com.notaryapp.model.webresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotaDoc {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("docuType")
    @Expose
    private String docuType;
    @SerializedName("docuName")
    @Expose
    private String docuName;
    @SerializedName("apnNum")
    @Expose
    private String apnNum;
    @SerializedName("tranid")
    @Expose
    private String tranid;
    @SerializedName("docname")
    @Expose
    private String docname;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("fileName")
    @Expose
    private String fileName;
    @SerializedName("photoName")
    @Expose
    private String photoName;
    @SerializedName("delete")
    @Expose
    private Boolean delete;
    @SerializedName("PagCount")
    @Expose
    private Integer pagCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDocuType() {
        return docuType;
    }

    public void setDocuType(String docuType) {
        this.docuType = docuType;
    }

    public String getDocuName() {
        return docuName;
    }

    public void setDocuName(String docuName) {
        this.docuName = docuName;
    }

    public String getApnNum() {
        return apnNum;
    }

    public void setApnNum(String apnNum) {
        this.apnNum = apnNum;
    }

    public String getTranid() {
        return tranid;
    }

    public void setTranid(String tranid) {
        this.tranid = tranid;
    }

    public String getDocname() {
        return docname;
    }

    public void setDocname(String docname) {
        this.docname = docname;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public Integer getPagCount() {
        return pagCount;
    }

    public void setPagCount(Integer pagCount) {
        this.pagCount = pagCount;
    }

}
