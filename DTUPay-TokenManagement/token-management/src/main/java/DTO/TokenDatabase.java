package dto;

import session.TokenDataBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class TokenDatabase {
    public HashMap<String, ArrayList<UUID>> TokenMap = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TokenDataBase)) {
            return false;
        }

        TokenDataBase tokenDataBase = (TokenDataBase) o;

        if(TokenMap.size() != tokenDataBase.TokenMap.size()){
            return false;
        }

        int count1 = 0;
        int count2 = 0;

        for (var entry : TokenMap.entrySet()
             ) {
            count1 = entry.getValue().size();
            count2 = tokenDataBase.TokenMap.get(entry.getKey()).size();
        }

        return count1 == count2;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "Test";
    }
}
