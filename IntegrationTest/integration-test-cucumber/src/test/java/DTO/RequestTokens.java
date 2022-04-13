package DTO;

import java.io.Serializable;

public class RequestTokens implements Serializable {
    private String cid;
    private Integer amount;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
