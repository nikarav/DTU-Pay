package services;

import messaging.implementations.RabbitMqQueue;

public class StartUp {

    public static void main(String[] args) throws Exception {
        new StartUp().startUp();
    }

    private void startUp() throws Exception {
        System.out.println("Account Management Service Starting...");

        var mq = new RabbitMqQueue("rabbitMq");
        new AccountService(mq);
    }
}
