package com.hj.study.spring.boot.config;


import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("server")
@Configuration
public class RabbitConfig {

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
		factory.setMaxConcurrentConsumers(10);

		return factory;
	}

	@Bean( name="requestExchange" )
	public TopicExchange requestExchange() {
		return new TopicExchange("request");
	}
	
	@Bean( name="chatExchange" )
	public TopicExchange chatExchange() {
		return new TopicExchange("chat");
	}
	
	@Bean( name="userExchange" )
	public TopicExchange userExchange() {
		return new TopicExchange("user");
	}
	
	@Bean( name="roomExchange" )
	public FanoutExchange roomExchange() {
		return new FanoutExchange("room");
	}

	@Bean
	public Queue commandQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue userQueue() {
		return new AnonymousQueue();
	}
	
	@Bean
	public Queue roomQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Binding commandBinding(TopicExchange requestExchange, Queue commandQueue) {
		return BindingBuilder.bind(commandQueue)
		    .to(requestExchange)
		    .with("command.#");
	}
	
	@Bean
	public Binding chatBinding(TopicExchange requestExchange, TopicExchange chatExchange) {
		return BindingBuilder.bind(chatExchange)
		    .to(requestExchange)
		    .with("chat.#");
	}

	@Bean
	public Binding userBinding(TopicExchange chatExchange, TopicExchange userExchange) {
		return BindingBuilder.bind(userExchange)
		    .to(chatExchange)
		    .with("*.user.#");
	}
	

	@Bean
	public Binding roomBinding(TopicExchange chatExchange, FanoutExchange roomExchange) {
		return BindingBuilder.bind(roomExchange)
		    .to(chatExchange)
		    .with("*.room.#");
	}
	
	@Bean
	public Binding roomQueueBinding(FanoutExchange roomExchange, Queue roomQueue) {
		return BindingBuilder.bind(roomQueue)
		    .to(roomExchange);
	}

}
