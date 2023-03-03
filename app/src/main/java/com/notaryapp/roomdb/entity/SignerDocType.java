package com.notaryapp.roomdb.entity;

import java.io.Serializable;

public class SignerDocType implements Serializable {

    String email;
    String id;
    String signertype;
    String tranid;
    String verifytype;

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

    public String getSignertype() {
        return signertype;
    }

    public void setSignertype(String signertype) {
        this.signertype = signertype;
    }

    public String getTranid() {
        return tranid;
    }

    public void setTranid(String tranid) {
        this.tranid = tranid;
    }

    public String getVerifytype() {
        return verifytype;
    }

    public void setVerifytype(String verifytype) {
        this.verifytype = verifytype;
    }
}
