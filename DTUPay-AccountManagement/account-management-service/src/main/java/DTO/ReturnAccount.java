package DTO;

import Entities.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "return_account")
@Data // Automatic getter and setters and equals etc
@NoArgsConstructor // Needed for JSON deserialization and XML serialization and deserialization
@AllArgsConstructor
public class ReturnAccount {
    private Account account;
    private boolean isSuccess;
    private String errorMessage;
}
