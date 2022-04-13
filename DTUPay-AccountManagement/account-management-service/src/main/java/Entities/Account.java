package Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "account")
@Data // Automatic getter and setters and equals etc
@NoArgsConstructor // Needed for JSON deserialization and XML serialization and deserialization
@AllArgsConstructor
public class Account {
    //id to identify account in DTU PAY
    private String accountId;
    private AccountType type;
    private String cprNumber;
    private String firstName;
    private String lastName;
    private String bankAccountId;
}
