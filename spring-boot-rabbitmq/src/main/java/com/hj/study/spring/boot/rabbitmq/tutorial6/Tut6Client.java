package com.hj.study.spring.boot.rabbitmq.tutorial6;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class Tut6Client {
	
	@Autowired
	private RabbitTemplate template;

	AtomicInteger count = new AtomicInteger(0);

	@Scheduled(fixedDelay = 5000, initialDelay = 500)
	public void send() {
		String message = "blocking message " + count.incrementAndGet();
		template.convertAndSend("tut.backoff.blocking", message);
		System.out.println(" [x] Requesting " + message);
	}
}
