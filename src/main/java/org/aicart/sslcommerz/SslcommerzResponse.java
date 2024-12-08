package org.aicart.sslcommerz;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SslcommerzResponse {

    private String status;
    private String tran_date;
    private String tran_id;
    private String val_id;
    private String amount;
    private String store_amount;
    private String currency;

    public boolean isValid() {
        if(status == null) return false;

        return status.equals("VALID") || status.equals("VALIDATED");
    }

    // Getters and setters for the required fields
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTran_date() {
        return tran_date;
    }

    public void setTran_date(String tran_date) {
        this.tran_date = tran_date;
    }

    public String getTran_id() {
        return tran_id;
    }

    public void setTran_id(String tran_id) {
        this.tran_id = tran_id;
    }

    public String getVal_id() {
        return val_id;
    }

    public void setVal_id(String val_id) {
        this.val_id = val_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStore_amount() {
        return store_amount;
    }

    public void setStore_amount(String store_amount) {
        this.store_amount = store_amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
