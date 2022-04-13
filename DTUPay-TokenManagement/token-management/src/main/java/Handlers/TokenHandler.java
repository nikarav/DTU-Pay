package handlers;
/*
authors:
Hejlsberg Jacob Kølbjerg - s194618
Laforce Erik Aske - s194620
 */

import java.util.ArrayList;
import java.util.UUID;

import session.*;

// TODO: If userId is not inside the Hashmap we need to check if the provided userId is legit (accountManagement)
public class TokenHandler {
    public ArrayList<UUID> generateTokens(String userId, Integer amountOfTokens) throws Exception {
        var existingTokens = TokenDataBase.TokenMap.get(userId);

        if (existingTokens == null){
            TokenDataBase.TokenMap.put(userId, new ArrayList<UUID>());
            existingTokens = TokenDataBase.TokenMap.get(userId);
        }

        if(existingTokens.size() > 1){
            throw new Exception("Customer already has more than 1 token");
        }

        if(amountOfTokens > 5 || amountOfTokens < 1){
            throw new Exception("Requested amount of tokens was out of range");
        }

        ArrayList<UUID> newTokens = new ArrayList<>();

        for (int i = 0; i < amountOfTokens; i ++){
            newTokens.add(UUID.randomUUID());
        }

        existingTokens.addAll(newTokens);

        return newTokens;
    }

    public String consumeToken(UUID uuid) throws Exception {
        for (var tokenList : TokenDataBase.TokenMap.entrySet()) {
            for (var token : tokenList.getValue()){
                if(token.equals(uuid)){
                    tokenList.getValue().remove(token);
                    return tokenList.getKey();
                }
            }
        }
        throw new Exception("Token does not exist");
    }
}
