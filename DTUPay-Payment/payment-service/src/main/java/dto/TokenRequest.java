package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "token_request")
@Data // Automatic getter and setters and equals etc
@NoArgsConstructor // Needed for JSON deserialization and XML serialization and deserialization
@AllArgsConstructor
public class TokenRequest {
    private String token;
}
