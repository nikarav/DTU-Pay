package DTO;


import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

public class ReturnTransactionInfo {
    public boolean isSuccess;
    public String errorMessage;
    public List<Transaction> transactions;
}