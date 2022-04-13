package services;
/*
authors:
Hejlsberg Jacob Kølbjerg - s194618
Laforce Erik Aske - s194620
 */
import dto.TokenDatabase;
import handlers.TokenHandler;
import session.TokenDataBase;
import messaging.Event;
import messaging.MessageQueue;

import java.util.ArrayList;
import java.util.UUID;

import utils.*;

public class TokenServices {
    private MessageQueue queue;
    private TokenHandler tokenHandler = new TokenHandler();

    public TokenServices(MessageQueue queue){
        this.queue = queue;
        this.queue.addHandler(EventTypes.TOKEN_REQUESTED, this::handleTokenRequest);
        this.queue.addHandler(EventTypes.USE_TOKEN_REQUESTED, this::handleUseToken);
        this.queue.addHandler(EventTypes.GET_DATA_REQUEST, this::handleGetData);
    }

    public Event handleGetData(Event event) {
        Event publishedEvent;

        TokenDatabase db = new TokenDatabase();
        db.TokenMap = TokenDataBase.TokenMap;

        publishedEvent = new Event(EventTypes.GET_DATA_SUCCESS, event.getCorrID(), new Object[]{db});
        queue.publish(publishedEvent);
        return publishedEvent;
    }

    public Event handleUseToken(Event event) {
        Event publishedEvent;

        var uuid = event.getArgument(0, UUID.class);

        try {
            String userId = tokenHandler.consumeToken(uuid);

            publishedEvent = new Event(EventTypes.USE_TOKEN_COMPLETED, event.getCorrID(), new Object[]{userId});
            queue.publish(publishedEvent);

            return publishedEvent;
        } catch (Exception e){

            publishedEvent = new Event(EventTypes.USE_TOKEN_FAILED, event.getCorrID(), new Object[]{e.getMessage()});
            queue.publish(publishedEvent);

            return publishedEvent;
        }
    }

    public Event handleTokenRequest(Event event) {
        Event publishedEvent;

        var userId = event.getArgument(0, String.class);
        var amountOfTokens = event.getArgument(1, Integer.class);

        try {
            ArrayList<UUID> tokens = tokenHandler.generateTokens(userId, amountOfTokens);

            int tokenCount = tokens.size();

            var eventObj = new Object[tokenCount + 1];

            eventObj[0] = tokenCount;

            for (int i = 1; i < tokenCount + 1; i ++){
                eventObj[i] = tokens.get(i - 1);
            }

            publishedEvent = new Event(EventTypes.TOKEN_REQUEST_COMPLETED, event.getCorrID(), eventObj);
            queue.publish(publishedEvent);
            return publishedEvent;
        } catch (Exception e){
            publishedEvent = new Event(EventTypes.TOKEN_REQUEST_FAILED, event.getCorrID(), new Object[]{e.getMessage()});
            queue.publish(publishedEvent);

            return publishedEvent;
        }
    }
}
