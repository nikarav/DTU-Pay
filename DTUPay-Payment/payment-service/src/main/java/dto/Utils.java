package dto;

import java.util.UUID;

public class Utils {
    public String generateTransationId(){
        return UUID.randomUUID().toString();
    }
}

