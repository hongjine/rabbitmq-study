package com.hj.study.spring.boot.listener;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.hj.study.spring.dto.Command;



@Profile("server")
@Component
//@RabbitListener(queues = "command")
@RabbitListener(queues = "command", containerFactory = "retryExchangeContainerFactory", ackMode = "MANUAL")
public class CommandListener {
	
	@Autowired
	private AmqpAdmin amqpAdmin;
    
//	@Autowired
//	private FanoutExchange roomExchage;
	
    @RabbitHandler
    public void receive(Command command) throws Exception {
        System.out.println(" [x] Received '" + command.getBody() + "'");
        
       if ("create".equals(command.getCommand())) {
    	   
    	   FanoutExchange roomNameFanout = new FanoutExchange("room." + command.getArguments()[0]);
    	   amqpAdmin.declareExchange(roomNameFanout);
    	   
    	   FanoutExchange roomFanout = new FanoutExchange("room");
    	   
    	   Binding roomBinding = BindingBuilder.bind(roomNameFanout)
    			   							.to(roomFanout);
    	   
    	   amqpAdmin.declareBinding(roomBinding);
    	   
       } else if ("invite".equals(command.getCommand())) {
    	   
    	   FanoutExchange fanoutExchange = new FanoutExchange("user." + command.getArguments()[1]);
    	   FanoutExchange roomExchange = new FanoutExchange("room." + command.getArguments()[0]);
    	   
    	   Binding roomBinding = BindingBuilder.bind(fanoutExchange)
						.to(roomExchange);
    	   
    	   amqpAdmin.declareBinding(roomBinding);
    	   
       }
       
    }
    
    @RabbitHandler
    public void receive(String in) {
        System.out.println(" [x] Received '" + in + "'");
    }	
}
