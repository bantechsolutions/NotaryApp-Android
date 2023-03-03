
package com.notaryapp.model.webresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SealResponse {

    @SerializedName("fileName")
    @Expose
    private String fileName;
    @SerializedName("fileDownloadUri")
    @Expose
    private String fileDownloadUri;
    @SerializedName("fileType")
    @Expose
    private String fileType;
    @SerializedName("size")
    @Expose
    private Integer size;
    @SerializedName("success")
    @Expose
    private String success;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

}
