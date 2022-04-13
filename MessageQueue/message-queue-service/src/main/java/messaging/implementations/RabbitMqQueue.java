package messaging.implementations;
/*
This repository is based on the coed provide
from Hubert Baumeister for the course 02267
@authors:
Nikos Karavasilis s213685
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import com.google.gson.Gson;
import com.rabbitmq.client.*;
import messaging.Event;
import messaging.MessageQueue;

public class RabbitMqQueue implements MessageQueue {

	private static final String TOPIC = "events";
	private static final String DEFAULT_HOSTNAME = "localhost";
	private static final String EXCHANGE_NAME = "eventsExchange";
	private static final String QUEUE_TYPE = "topic";


	private Channel channel;
	private String hostname;

	public RabbitMqQueue() {
		this(DEFAULT_HOSTNAME);
	}

	public RabbitMqQueue(String hostname) {
		this.hostname = hostname;
		channel = setUpChannel();
	}


	@Override
	public void publish(Event event) {
		String message = new Gson().toJson(event);
		System.out.println("[x] Publish event " + message);
		AMQP.BasicProperties props = new AMQP.BasicProperties
				.Builder()
				.correlationId(event.getCorrID())
				.build();

		try {
			channel.basicPublish(EXCHANGE_NAME, TOPIC, props, message.getBytes("UTF-8"));
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	private Channel setUpChannel() {
		Channel chan;
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(hostname);
			Connection connection = factory.newConnection();
			chan = connection.createChannel();
			chan.exchangeDeclare(EXCHANGE_NAME, QUEUE_TYPE);
		} catch (IOException | TimeoutException e) {
			throw new Error(e);
		}
		return chan;
	}


	@Override
	public void addHandler(String eventType, Consumer<Event> handler) {
		var chan = setUpChannel();
		System.out.println("[x] handler " + handler + " for event type " + eventType + " installed");
		try {
			String queueName = chan.queueDeclare().getQueue();
			chan.queueBind(queueName, EXCHANGE_NAME, TOPIC);

			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String message = new String(delivery.getBody(), "UTF-8");

				System.out.println("[x] handle event " + message);

				Event event = new Gson().fromJson(message, Event.class);

				if (eventType.equals(event.getType())
						&& delivery.getProperties().getCorrelationId() != null
						&& event.getCorrID().equals(delivery.getProperties().getCorrelationId())) {
					handler.accept(event);
				}
			};
			chan.basicConsume(queueName, true, deliverCallback, consumerTag -> {
			});
		} catch (IOException e1) {
			throw new Error(e1);
		}
	}

}