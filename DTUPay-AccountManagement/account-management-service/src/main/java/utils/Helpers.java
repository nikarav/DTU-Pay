package utils;

import java.util.UUID;

public class Helpers {
    public String generateCorrelationID(){
        return UUID.randomUUID().toString();
    }

}