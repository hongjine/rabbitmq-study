package com.hj.study.spring.boot.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.ErrorHandler;

import com.hj.study.spring.boot.error.CustomErrorHandler;
import com.hj.study.spring.boot.error.CustomFatalExceptionStrategy;

@Profile("client")
@Configuration
public class RabbitClientConfig {

	@Autowired
	private RabbitProperties rabbitProperties;

	//4th hw : setting Json messageConverter
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
	public RabbitAdmin amqpAdmin(ConnectionFactory connectionFactory) {
		return new RabbitAdmin(connectionFactory);
	}
	
	@Bean
	public SimpleRabbitListenerContainerFactory chatListenerContainerFactory(
			ConnectionFactory connectionFactory,
			MessageConverter messageConverter) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(messageConverter);
		factory.setErrorHandler(rejectErrorHandler());
		factory.setPrefetchCount(250);
		factory.setConcurrentConsumers(1);
		factory.setMaxConcurrentConsumers(1);

		return factory;
	}
	
	//4th hw : setting Json messageConverter
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(messageConverter);
		return template;
	}

	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(messageConverter);  //4th hw : setting Json messageConverter
		factory.setErrorHandler(rejectErrorHandler());
		factory.setPrefetchCount(3);
		factory.setConcurrentConsumers(3);
		factory.setMaxConcurrentConsumers(3);

//		factory.setPrefetchCount(1);
//		factory.setConcurrentConsumers(1);
//		factory.setMaxConcurrentConsumers(1);
		
		return factory;
	}
	
	@Bean
	public ErrorHandler customErrorHandler() {
		return new CustomErrorHandler();
	}

	@Bean
	public ErrorHandler rejectErrorHandler() {
		return new ConditionalRejectingErrorHandler(customExceptionStrategy());
	}

	@Bean
	public FatalExceptionStrategy customExceptionStrategy() {
		return new CustomFatalExceptionStrategy();
	}
	
	//4th hw : setting dead letter queue connect my queue
	@Bean
	public Queue myUserQueue() {
//		return new Queue("user." + rabbitProperties.getUsername());		
		return QueueBuilder.durable("user." + rabbitProperties.getUsername())
				.deadLetterExchange("")
				.deadLetterRoutingKey("dead-letter").build();
	}
	
	//
	@Bean
	public FanoutExchange userFanoutExchange() {
		return new FanoutExchange("user." + rabbitProperties.getUsername());
	}
	
	@Bean
	public Binding userToUserFanoutBinding(FanoutExchange userFanoutExchange) {
		return BindingBuilder.bind(userFanoutExchange)
		    .to(new TopicExchange("user"))
//		    .with("chat.user." + rabbitProperties.getUsername());
		    .with("*.user." + rabbitProperties.getUsername());
	}
	
	@Bean
	public Binding userFanoutToUserQueue(FanoutExchange userFanoutExchange, Queue myUserQueue) {
		return BindingBuilder.bind(myUserQueue)
		    .to(userFanoutExchange);
//		    .with("chat.user." + rabbitProperties.getUsername());
		    
	}

}
