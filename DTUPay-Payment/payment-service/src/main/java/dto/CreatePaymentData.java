package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "payment_data")
@Data // Automatic getter and setters and equals etc
@NoArgsConstructor // Needed for JSON deserialization and XML serialization and deserialization
@AllArgsConstructor
public class CreatePaymentData {
    private double amount;
    private String token;
    private String customerBankAccountId;
    private String merchantBankAccountId;
    private String description;
}

