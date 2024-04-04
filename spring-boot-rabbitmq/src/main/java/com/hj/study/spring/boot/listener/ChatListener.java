package com.hj.study.spring.boot.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("server")
@Component
public class ChatListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		System.out.println(Thread.currentThread().getId() + " [x] Received Chat-hash: " + message.toString());
	}
	
}