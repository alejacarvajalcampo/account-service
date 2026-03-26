package com.sofka.accountservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;

@Configuration
public class RabbitMqConfig {

    public static final String CUSTOMER_EXCHANGE = "customer.exchange";
    public static final String CUSTOMER_QUEUE = "customer.account.queue";
    public static final String CUSTOMER_ROUTING_KEY = "customer.sync";
    public static final String CUSTOMER_DLX = "customer.exchange.dlx";
    public static final String CUSTOMER_DLQ = "customer.account.queue.dlq";
    public static final String CUSTOMER_DLQ_ROUTING_KEY = "customer.sync.dlq";

    @Bean
    public DirectExchange customerExchange() {
        return new DirectExchange(CUSTOMER_EXCHANGE);
    }

    @Bean
    public DirectExchange customerDeadLetterExchange() {
        return new DirectExchange(CUSTOMER_DLX);
    }

    @Bean
    public Queue customerQueue() {
        return QueueBuilder.durable(CUSTOMER_QUEUE)
                .deadLetterExchange(CUSTOMER_DLX)
                .deadLetterRoutingKey(CUSTOMER_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue customerDeadLetterQueue() {
        return QueueBuilder.durable(CUSTOMER_DLQ).build();
    }

    @Bean
    public Binding customerBinding(Queue customerQueue, DirectExchange customerExchange) {
        return BindingBuilder.bind(customerQueue).to(customerExchange).with(CUSTOMER_ROUTING_KEY);
    }

    @Bean
    public Binding customerDeadLetterBinding(Queue customerDeadLetterQueue, DirectExchange customerDeadLetterExchange) {
        return BindingBuilder.bind(customerDeadLetterQueue).to(customerDeadLetterExchange).with(CUSTOMER_DLQ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory customerSyncListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .recoverer((args, cause) -> { throw new AmqpRejectAndDontRequeueException("Retries agotados", cause); })
                .build());
        return factory;
    }
}

