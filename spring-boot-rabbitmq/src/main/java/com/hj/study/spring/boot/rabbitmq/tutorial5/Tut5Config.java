package com.hj.study.spring.boot.rabbitmq.tutorial5;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"tut5", "rpc"})
@Configuration
public class Tut5Config {
	
	@Profile("sender")
	private static class ClientConfig {

		@Bean
		public DirectExchange tut5Exchange() {
			return new DirectExchange("tut.rpc");
		}

		@Bean
		public Tut5Client client() {
	 	 	return new Tut5Client();
		}

	}

	@Profile("receiver")
	private static class ServerConfig {

		@Bean( name="tut5Queue" )
		public Queue tut5Queue() {
			return new Queue("tut.rpc.requests");
		}

		@Bean( name="tut5Exchange" )
		public DirectExchange tut5Exchange() {
			return new DirectExchange("tut.rpc");
		}

		@Bean
		public Binding tut5ExchangeBinding(DirectExchange tut5Exchange, Queue tut5Queue) {
			return BindingBuilder.bind(tut5Queue)
			    .to(tut5Exchange)
			    .with("rpc");
		}

		@Bean
		public Tut5Server server() {
			return new Tut5Server();
		}

	}
}