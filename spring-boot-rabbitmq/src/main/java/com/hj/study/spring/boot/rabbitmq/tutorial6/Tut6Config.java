package com.hj.study.spring.boot.rabbitmq.tutorial6;

import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Profile({ "tut6", "backoff" })
@Configuration
public class Tut6Config {

	@Profile("sender")
	private static class ClientConfig {

		@Bean
		public DirectExchange exchange() {
			return new DirectExchange("tut.rpc");
		}

		@Bean
		public Tut6Client tut6Client() {
			return new Tut6Client();
		}

	}

	@Profile("receiver")
	private static class ServerConfig {

		@Bean
		public Queue blockingQueue() {
			return QueueBuilder.nonDurable("tut.backoff.blocking").build();
		}

		@Bean
		public RetryOperationsInterceptor retryInterceptor() {
			return RetryInterceptorBuilder.stateless().backOffOptions(1000, 3.0, 10000).maxAttempts(5).build();
		}

		@Bean
		public SimpleRabbitListenerContainerFactory retryContainerFactory(ConnectionFactory connectionFactory,
				RetryOperationsInterceptor retryInterceptor) {
			SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
			factory.setConnectionFactory(connectionFactory);

			Advice[] adviceChain = { retryInterceptor };
			factory.setAdviceChain(adviceChain);

			return factory;
		}

		@Bean
		public Tut6Server tut6Server() {
			return new Tut6Server();
		}

	}

}