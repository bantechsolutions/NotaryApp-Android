
package com.notaryapp.model.webresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransByPage {

    @SerializedName("Transactions")
    @Expose
    private Transactions transactions;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("success")
    @Expose
    private String success;

    public Transactions getTransactions() {
        return transactions;
    }

    public void setTransactions(Transactions transactions) {
        this.transactions = transactions;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

}
