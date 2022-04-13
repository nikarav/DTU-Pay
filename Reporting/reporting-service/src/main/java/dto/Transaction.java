package dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "transaction")
@Data // Automatic getter and setters and equals etc
@NoArgsConstructor // Needed for JSON deserialization and XML serialization and deserialization
@AllArgsConstructor
public class Transaction {
    private String transactionId;
    private double amount;
    private String token;
    private String customerBankId;
    private String merchantBankId;
    private String description;
    private LocalDateTime time;
}
