package DTO;

import java.time.LocalDateTime;
import javax.xml.bind.annotation.XmlRootElement;


public class Transaction {
    public String transactionId;
    public double amount;
    public String token;
    public String customerBankId;
    public String merchantBankId;
    public String description;
    public LocalDateTime time;
}