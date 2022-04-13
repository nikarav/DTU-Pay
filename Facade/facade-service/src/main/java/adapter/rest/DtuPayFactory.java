package adapter.rest;
import messaging.implementations.RabbitMqQueue;
import utils.*;
import service.DtuPayService;


public class DtuPayFactory<T> {
    static DtuPayService dtuPayService = null;

    public DtuPayService<T> getDtuPayService() {
        // The singleton pattern.
        // Ensure that there is at most
        // one instance of a PaymentService
        if (dtuPayService != null) {
            System.out.println("heheheheh");
            return dtuPayService;
        }

        // Hookup the classes to send and receive
        // messages via RabbitMq, i.e. RabbitMqSender and
        // RabbitMqListener.
        // This should be done in the factory to avoid
        // the PaymentService knowing about them. This
        // is called dependency injection.
        // At the end, we can use the PaymentService in tests
        // without sending actual messages to RabbitMq.
        var mq = new RabbitMqQueue("rabbitMq");

        dtuPayService = new DtuPayService<T>(mq);

        return dtuPayService;
    }


}
