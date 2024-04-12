package com.hj.study.spring.boot.error;


import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.rabbitmq.client.Channel;

public class RetryExchangeInterceptor implements MethodInterceptor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private RabbitTemplate rabbitTemplate;

    private RetryExchange retryQueue;

    public RetryExchangeInterceptor(RabbitTemplate rabbitTemplate, RetryExchange retryQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.retryQueue = retryQueue;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return tryConsume(invocation, this::ack, (messageAndChannel, e) -> {
            try {
                logger.error("Processing message from : {}",
                        ((Message) invocation.getArguments()[1]).getMessageProperties().getConsumerQueue());
                int retryCount = tryGetRetryCountOrFail(messageAndChannel, e);
                sendToRetryExchange(messageAndChannel, retryCount);
            } catch (Throwable t) {
                // ...
                throw new RuntimeException(t);
            }
        });
    }

    private Object tryConsume(MethodInvocation invocation, Consumer<MessageAndChannel> successHandler,
            BiConsumer<MessageAndChannel, Throwable> errorHandler) throws Throwable {
        MessageAndChannel mac = new MessageAndChannel((Message) invocation.getArguments()[1],
                (Channel) invocation.getArguments()[0]);
        Object ret = null;
        try {
            ret = invocation.proceed();
            successHandler.accept(mac);
        } catch (Throwable e) {
            errorHandler.accept(mac, e);
        }
        return ret;
    }

    private void ack(MessageAndChannel mac) {
        try {
            mac.channel.basicAck(mac.message.getMessageProperties()
                    .getDeliveryTag(), false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int tryGetRetryCountOrFail(MessageAndChannel mac, Throwable originalError) throws Throwable {
        MessageProperties props = mac.message.getMessageProperties();

        String xRetriedCountHeader = (String) props.getHeader("x-retried-count");
        final int xRetriedCount = xRetriedCountHeader == null ? 0 : Integer.valueOf(xRetriedCountHeader);

        if (retryQueue.retriesExhausted(xRetriedCount)) {
            mac.channel.basicReject(props.getDeliveryTag(), false);

            throw originalError;
        }

        return xRetriedCount;
    }

    private void sendToRetryExchange(MessageAndChannel mac, int retryCount) throws Exception {
        rabbitTemplate.convertAndSend(retryQueue.getRetryExchange().getName(),
                mac.message.getMessageProperties().getReceivedRoutingKey(), mac.message, m -> {
                    MessageProperties props = m.getMessageProperties();
                    props.setExpiration(String.valueOf(retryQueue.getTimeToWait(retryCount)));
                    props.setHeader("x-retried-count", String.valueOf(retryCount + 1));

                    return m;
                });

        mac.channel.basicReject(mac.message.getMessageProperties()
                .getDeliveryTag(), false);
    }

    private class MessageAndChannel {
        private Message message;
        private Channel channel;

        private MessageAndChannel(Message message, Channel channel) {
            this.message = message;
            this.channel = channel;
        }
    }
}