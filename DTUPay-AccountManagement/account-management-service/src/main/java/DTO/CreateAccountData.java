package DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "create_account_data")
@Data // Automatic getter and setters and equals etc
@NoArgsConstructor // Needed for JSON deserialization and XML serialization and deserialization
@AllArgsConstructor
public class CreateAccountData {
    //0 is customer
    //1 is merchant
    private int type;
    private String cprNumber;
    private String firstName;
    private String lastName;
    private String bankAccountId;
}
