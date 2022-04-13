package services;

import messaging.implementations.RabbitMqQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartUp {

    private static final Logger log
            = LoggerFactory.getLogger(StartUp.class);

    public static void main(String[] args) throws Exception {
        new StartUp().startUp();
    }

    private void startUp() throws Exception {
        log.info("Payment Service starting...");
        var mqPayment = new RabbitMqQueue("rabbitMq");
        new PaymentService(mqPayment);
    }

}
