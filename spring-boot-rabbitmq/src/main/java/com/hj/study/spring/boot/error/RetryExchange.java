package com.hj.study.spring.boot.error;

import org.springframework.amqp.core.Exchange;

public class RetryExchange {
	private Exchange exchange;
    private long initialInterval;
    private double multiplier;
    private long maxInterval;
	private Long maxAttempts;

    public RetryExchange(long initialInterval, double multiplier, long maxInterval, Exchange exchange) {
        this.exchange = exchange;
        this.initialInterval = initialInterval;
        this.multiplier = multiplier;
        this.maxInterval = maxInterval;
    }

    public RetryExchange(long initialInterval, double multiplier, long maxInterval, long maxAttempts, Exchange exchange) {
        this.exchange = exchange;
        this.initialInterval = initialInterval;
        this.multiplier = multiplier;
        this.maxInterval = maxInterval;
		this.maxAttempts = maxAttempts;
    }

    public Exchange getRetryExchange() {
        return this.exchange;
    }

    public boolean retriesExhausted(int retry) {
        return maxAttempts != null && retry >= maxAttempts;
    }

    public long getTimeToWait(int retry) {
        double time = initialInterval * Math.pow(multiplier, (double) retry);
        if (time > maxInterval) {
            return maxInterval;
        }

        return (long) time;
    }
}
