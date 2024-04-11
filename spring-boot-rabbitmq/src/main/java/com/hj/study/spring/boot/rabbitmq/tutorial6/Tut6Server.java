package com.hj.study.spring.boot.rabbitmq.tutorial6;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class Tut6Server {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@RabbitListener(queues = "tut.backoff.blocking", containerFactory = "retryContainerFactory")
	public void consumeBlocking(String payload) throws Exception {
		logger.error("Processing message from blocking-queue: {}", payload);
	
		throw new Exception("exception occured!");
	}
}
