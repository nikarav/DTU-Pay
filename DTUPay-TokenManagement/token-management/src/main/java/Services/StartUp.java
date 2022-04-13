package services;

import messaging.implementations.RabbitMqQueue;


public class StartUp {

    public static void main(String[] args) throws Exception {
        new StartUp().startUp();
    }

    private void startUp() throws Exception {
        System.out.println("startup Token Management");
        var mqTokenManagement = new RabbitMqQueue("rabbitMq");
        new TokenServices(mqTokenManagement);
    }
}
