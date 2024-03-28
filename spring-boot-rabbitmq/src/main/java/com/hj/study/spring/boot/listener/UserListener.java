package com.hj.study.spring.boot.listener;


import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("client")
@Component
@RabbitListener(queues = "#{'user.'.concat('${spring.rabbitmq.username}')}")
public class UserListener {
	
    @RabbitHandler
    public void receive(String in) {
        System.out.println(" [x] Received '" + in + "'");
    }	
}
