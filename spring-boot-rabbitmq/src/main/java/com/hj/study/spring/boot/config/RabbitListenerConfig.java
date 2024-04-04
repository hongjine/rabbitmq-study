package com.hj.study.spring.boot.config;

import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.hj.study.spring.boot.listener.ChatListener;

@Profile("server")
@Configuration
public class RabbitListenerConfig implements RabbitListenerConfigurer {

	@Value("${rabbitmq.server.chat-concurrent}") int concurrent;

	@Autowired
	private ChatListener chatListener;

	@Autowired
	SimpleRabbitListenerContainerFactory chatListenerContainerFactory;

	@Override
	public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {

		for (int i = 0; i < concurrent; i++) {
            String queueName = "chat." + Integer.toString(i);
			SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
            endpoint.setId(queueName);
			endpoint.setQueueNames(queueName);
			endpoint.setMessageListener(chatListener);
			endpoint.setExclusive(false);
			
			registrar.registerEndpoint(endpoint, chatListenerContainerFactory);
		}
	}
	
}