package DTO;

import java.util.UUID;


public class Utils {
    public String generateAccountId(){
        return UUID.randomUUID().toString();
    }
}


