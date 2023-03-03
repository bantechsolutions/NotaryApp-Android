
package com.notaryapp.model.webresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Body {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("userNameN")
    @Expose
    private String userNameN;
    @SerializedName("tranType")
    @Expose
    private String tranType;
    @SerializedName("doj")
    @Expose
    private String doj;
    @SerializedName("star")
    @Expose
    private Boolean star;
    @SerializedName("tranid")
    @Expose
    private String tranid;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("level")
    @Expose
    private String level;
    @SerializedName("docType")
    @Expose
    private String docType;
    @SerializedName("signer")
    @Expose
    private List<Signer> signer = null;
    @SerializedName("witness")
    @Expose
    private List<Object> witness = null;
    @SerializedName("notaDoc")
    @Expose
    private List<NotaDoc> notaDoc = null;
    @SerializedName("docSigned")
    @Expose
    private Boolean docSigned;
    @SerializedName("seal")
    @Expose
    private Seal seal;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("stichedDoc")
    @Expose
    private String stichedDoc;
    @SerializedName("witnessCount")
    @Expose
    private Integer witnessCount;
    @SerializedName("signerdoctype")
    @Expose
    private List<Object> signerdoctype = null;

    @SerializedName("docsCount")
    @Expose
    private Integer docsCount;


    public Integer getDocsCount() {
        return docsCount;
    }

    public void setDocsCount(Integer docsCount) {
        this.docsCount = docsCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserNameN() {
        return userNameN;
    }

    public void setUserNameN(String userNameN) {
        this.userNameN = userNameN;
    }

    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
    }

    public String getDoj() {
        return doj;
    }

    public void setDoj(String doj) {
        this.doj = doj;
    }

    public Boolean getStar() {
        return star;
    }

    public void setStar(Boolean star) {
        this.star = star;
    }

    public String getTranid() {
        return tranid;
    }

    public void setTranid(String tranid) {
        this.tranid = tranid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public List<Signer> getSigner() {
        return signer;
    }

    public void setSigner(List<Signer> signer) {
        this.signer = signer;
    }

    public List<Object> getWitness() {
        return witness;
    }

    public void setWitness(List<Object> witness) {
        this.witness = witness;
    }

    public List<NotaDoc> getNotaDoc() {
        return notaDoc;
    }

    public void setNotaDoc(List<NotaDoc> notaDoc) {
        this.notaDoc = notaDoc;
    }

    public Boolean getDocSigned() {
        return docSigned;
    }

    public void setDocSigned(Boolean docSigned) {
        this.docSigned = docSigned;
    }

    public Seal getSeal() {
        return seal;
    }

    public void setSeal(Seal seal) {
        this.seal = seal;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getStichedDoc() {
        return stichedDoc;
    }

    public void setStichedDoc(String stichedDoc) {
        this.stichedDoc = stichedDoc;
    }

    public Integer getWitnessCount() {
        return witnessCount;
    }

    public void setWitnessCount(Integer witnessCount) {
        this.witnessCount = witnessCount;
    }

    public List<Object> getSignerdoctype() {
        return signerdoctype;
    }

    public void setSignerdoctype(List<Object> signerdoctype) {
        this.signerdoctype = signerdoctype;
    }

}
