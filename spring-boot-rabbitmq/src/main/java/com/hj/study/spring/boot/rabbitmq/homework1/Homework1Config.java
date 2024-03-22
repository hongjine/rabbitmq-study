package com.hj.study.spring.boot.rabbitmq.homework1;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"homework1"})
@Configuration
public class Homework1Config {

//	@Bean
//	public Declarables exchanges() {
//		return new Declarables(
//					new TopicExchange("request"),
//					new TopicExchange("chat"),
//					new TopicExchange("user"),
//					new TopicExchange("room")
//				);
//	}
	
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
	public TopicExchange roomExchange() {
		return new TopicExchange("room");
	}

	@Profile("receiver")
	private static class ReceiverConfig {

		@Bean
		public Homework1Receiver receiver() {
	 	 	return new Homework1Receiver();
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
		public Binding userBinding(TopicExchange chatExchange, Queue userQueue) {
			return BindingBuilder.bind(userQueue)
			    .to(chatExchange)
			    .with("*.user.#");
		}

		@Bean
		public Binding roomBinding(TopicExchange chatExchange, Queue roomQueue) {
			return BindingBuilder.bind(roomQueue)
			    .to(chatExchange)
			    .with("*.room.#");
		}

	}

	@Profile("sender")
	@Bean
	public Homework1Sender sender() {
		return new Homework1Sender();
	}

}