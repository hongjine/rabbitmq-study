package com.hj.study.spring.boot.rabbitmq.homework1;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class Homework1Receiver {

	@RabbitListener(queues = "#{commandQueue.name}")
	public void receiveCommand(String in) {
		receive(in, "receiveCommand");
	}

	@RabbitListener(queues = "#{userQueue.name}")
	public void receiveUser(String in) {
		receive(in, "receiveUser");
	}
	
	@RabbitListener(queues = "#{roomQueue.name}")
	public void receiveRoom(String in) {
		receive(in, "receiveRoom");
	}

	public void receive(String in, String receiver) {
		System.out.println("instance " + receiver + " [x] Received '" + in + "'");
	}
}