package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "request_payment_data")
@Data // Automatic getter and setters and equals etc
@NoArgsConstructor // Needed for JSON deserialization and XML serialization and deserialization
@AllArgsConstructor
public class RequestPaymentData {
    private double amount;
    private String token;
    private String customerAccountId;
    private String merchantAccountId;
    private String description;
}