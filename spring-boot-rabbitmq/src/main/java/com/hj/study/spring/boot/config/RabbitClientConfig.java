package com.hj.study.spring.boot.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("client")
@Configuration
public class RabbitClientConfig {

	@Autowired
	private RabbitProperties rabbitProperties;

	@Bean
	public RabbitAdmin amqpAdmin(ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}

	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setPrefetchCount(1);
		factory.setConcurrentConsumers(1);
		factory.setMaxConcurrentConsumers(1);

		return factory;
	}
	
	@Bean
	public Queue myUserQueue() {
		return new Queue("user." + rabbitProperties.getUsername());
	}
	
	@Bean
	public Binding userBinding(Queue myUserQueue) {
		return BindingBuilder.bind(myUserQueue)
		    .to(new TopicExchange("user"))
		    .with("chat.user." + rabbitProperties.getUsername());
	}

}
