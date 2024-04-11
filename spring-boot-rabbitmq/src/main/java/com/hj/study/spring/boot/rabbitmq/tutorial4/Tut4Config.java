package com.hj.study.spring.boot.rabbitmq.tutorial4;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"tut4","topics"})
@Configuration
public class Tut4Config {

	@Bean
	public TopicExchange topic() {
		return new TopicExchange("tut.topic");
	}

	@Profile("receiver")
	private static class ReceiverConfig {

		@Bean
		public Tut4Receiver receiver() {
	 	 	return new Tut4Receiver();
		}

		@Bean
		public Queue autoDeleteQueue1() {
			return new AnonymousQueue();
		}

		@Bean
		public Queue autoDeleteQueue2() {
			return new AnonymousQueue();
		}

		@Bean
		public Binding binding1a(TopicExchange topic,
		    Queue autoDeleteQueue1) {
			return BindingBuilder.bind(autoDeleteQueue1)
			    .to(topic)
			    .with("*.orange.*");
		}

		@Bean
		public Binding binding1b(TopicExchange topic,
		    Queue autoDeleteQueue1) {
			return BindingBuilder.bind(autoDeleteQueue1)
			    .to(topic)
			    .with("*.*.rabbit");
		}

		@Bean
		public Binding binding2a(TopicExchange topic,
		    Queue autoDeleteQueue2) {
			return BindingBuilder.bind(autoDeleteQueue2)
			    .to(topic)
			    .with("lazy.#");
		}

	}

	@Profile("sender")
	@Bean
	public Tut4Sender sender() {
		return new Tut4Sender();
	}

}