package DTO;

import java.io.Serializable;

public class RequestPaymentData implements Serializable {
    private double amount;
    private String token;
    private String customerAccountId;
    private String merchantAccountId;
    private String description;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCustomerAccountId() {
        return customerAccountId;
    }

    public void setCustomerAccountId(String customerAccountId) {
        this.customerAccountId = customerAccountId;
    }

    public String getMerchantAccountId() {
        return merchantAccountId;
    }

    public void setMerchantAccountId(String merchantBankAccountId) {
        this.merchantAccountId = merchantBankAccountId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "RequestPaymentData{" +
                "amount=" + amount +
                ", token='" + token + '\'' +
                ", customerAccountId='" + customerAccountId + '\'' +
                ", merchantBankAccountId='" + merchantAccountId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

