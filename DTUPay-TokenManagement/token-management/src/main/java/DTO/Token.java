package dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "account_request")
@Data // Automatic getter and setters and equals etc
@NoArgsConstructor // Needed for JSON deserialization and XML serialization and deserialization
@AllArgsConstructor
public class Token {
    private UUID uuid = UUID.randomUUID();
}
