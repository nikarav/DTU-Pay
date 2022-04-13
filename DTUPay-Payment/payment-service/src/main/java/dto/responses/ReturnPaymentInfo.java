package dto.responses;

import java.io.Serializable;

public class ReturnPaymentInfo implements Serializable {
    private boolean isSuccess;
    private String errorMessage;
    private String bankAccountIdCustomer;
    private String bankAccountIdMerchant;

    public ReturnPaymentInfo(boolean isSuccess, String errorMessage, String bankAccountIdCustomer, String bankAccountIdMerchant) {
        this.isSuccess = isSuccess;
        this.errorMessage = errorMessage;
        this.bankAccountIdCustomer = bankAccountIdCustomer;
        this.bankAccountIdMerchant = bankAccountIdMerchant;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getBankAccountIdCustomer() {
        return bankAccountIdCustomer;
    }

    public void setBankAccountIdCustomer(String bankAccountIdCustomer) {
        this.bankAccountIdCustomer = bankAccountIdCustomer;
    }

    public String getBankAccountIdMerchant() {
        return bankAccountIdMerchant;
    }

    public void setBankAccountIdMerchant(String bankAccountIdMerchant) {
        this.bankAccountIdMerchant = bankAccountIdMerchant;
    }
}
