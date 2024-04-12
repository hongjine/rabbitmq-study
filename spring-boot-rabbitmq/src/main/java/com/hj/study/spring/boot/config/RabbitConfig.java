package com.hj.study.spring.boot.config;


import java.util.ArrayList;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.hj.study.spring.boot.error.RetryExchange;
import com.hj.study.spring.boot.error.RetryExchangeInterceptor;

@Profile("server")
@Configuration
public class RabbitConfig {
	
	final int MAX_INTERVAL = 10000;

	@Bean
	public RabbitAdmin amqpAdmin(ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}
	
	@Bean
	public MessageConverter messageConverter() {
		ContentTypeDelegatingMessageConverter converter = new ContentTypeDelegatingMessageConverter(
				new Jackson2JsonMessageConverter());
		
		MessageConverter simple = (MessageConverter) new SimpleMessageConverter();
		converter.addDelegate("text/plain", simple);
		converter.addDelegate(null, simple);

		return converter;
	}

	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(messageConverter);
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
	public CustomExchange chatHashExchange() {
		return new CustomExchange("chat-hash", "x-consistent-hash");
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
	
	//4th hw : setting dead letter queue
	@Bean
	public Queue deadLetterQueue() {
		return new Queue("dead-letter");
	}
	
	@Bean
	public Declarables chatHashQueueAndBindings(@Value("${rabbitmq.server.chat-concurrent}") int concurrent,
			CustomExchange chatHash) {
		List<Declarable> declarables = new ArrayList<>(concurrent * 2);

		for (int i = 0; i < concurrent; i++) {
			Queue queue = new Queue("chat." + i);
			declarables.add(queue);
			declarables.add(new Binding(queue.getName(), DestinationType.QUEUE, chatHash.getName(), "1", null));
		}

		return new Declarables(declarables);
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
	public Binding chatHashBinding(TopicExchange requestExchange, CustomExchange chatHashExchange) {
		return BindingBuilder.bind(chatHashExchange)
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
	
	/// retry
	@Bean
	public RetryExchange retryExchange(FanoutExchange commandRetryExchange) {
		return new RetryExchange(1000, 3, MAX_INTERVAL, 5, commandRetryExchange);
	}
	
	@Bean
    public RetryExchangeInterceptor retryExchangeInterceptor(RabbitTemplate rabbitTemplate, RetryExchange retryExchange) {
        return new RetryExchangeInterceptor(rabbitTemplate, retryExchange);
    }

	@Bean
	public SimpleRabbitListenerContainerFactory retryExchangeContainerFactory(ConnectionFactory connectionFactory,
			MessageConverter messageConverter,
			RetryExchangeInterceptor retryInterceptor) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(messageConverter);
		Advice[] adviceChain = { retryInterceptor };
		factory.setAdviceChain(adviceChain);

		return factory;
	}

	@Bean
	public FanoutExchange commandRetryExchange() {
		return new FanoutExchange("command.retry");
	}

	@Bean
	public Queue commandRetryQueue() {
		return QueueBuilder.durable("command.retry").ttl(MAX_INTERVAL).deadLetterExchange("request").build();
	}

	@Bean
	public Binding bindingCommandRetry(FanoutExchange commandRetryExchange, Queue commandRetryQueue) {
		return BindingBuilder.bind(commandRetryQueue).to(commandRetryExchange);
	}
}
