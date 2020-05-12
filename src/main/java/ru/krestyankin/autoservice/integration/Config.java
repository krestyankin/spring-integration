package ru.krestyankin.autoservice.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.advice.ExpressionEvaluatingRequestHandlerAdvice;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.krestyankin.autoservice.models.Order;
import ru.krestyankin.autoservice.models.OrderItem;
import ru.krestyankin.autoservice.models.SparePart;


@Configuration
public class Config {
    private static final int RETRY_ATTEMPTS=3;
    private static final int RETRY_INTERVAL=200;

    @Bean(name = PollerMetadata.DEFAULT_POLLER )
    public PollerMetadata poller () {
        return Pollers.fixedRate(50).maxMessagesPerPoll(10).get() ;
    }

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(8);
        return threadPoolTaskExecutor;
    }

    @Bean
    public DirectChannel ordersInputChannel() {
        return MessageChannels.direct().get();
    }

    public class OrderSplitter extends AbstractMessageSplitter {
        @Override
        protected Object splitMessage(Message<?> message) {
            return ((Order)message.getPayload()).getItems();
        }
    }

    @Bean
    public IntegrationFlow autoserviceFlow(ThreadPoolTaskExecutor threadPoolTaskExecutor,
                                           RequestHandlerRetryAdvice retryAdvice,
                                           ExpressionEvaluatingRequestHandlerAdvice errorAdvice) {

        return IntegrationFlows.from("ordersInputChannel")
                .split()
                .channel(MessageChannels.executor(threadPoolTaskExecutor))
                .publishSubscribeChannel(orderSubs -> orderSubs.subscribe(
                        orderFlow -> orderFlow.split(new OrderSplitter())
                                .<Object, Boolean>route(p -> p instanceof SparePart, m -> m
                                        .subFlowMapping(true, sf->sf
                                                .handle("partService", "book")
                                        )
                                        .subFlowMapping(false, sf->sf
                                                .handle("servService", "book")
                                        )
                                        .advice(errorAdvice, retryAdvice)
                                )
                        )
                )
                .handle("orderService", "calcTotal")
                .aggregate()
                .get();
    }

    @Bean
    public SimpleRetryPolicy retryPolicy(){
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(RETRY_ATTEMPTS);
        return retryPolicy;
    }

    @Bean
    public FixedBackOffPolicy fixedBackOffPolicy(){
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(RETRY_INTERVAL);
        return fixedBackOffPolicy;
    }

    @Bean
    public RequestHandlerRetryAdvice retryAdvice(SimpleRetryPolicy retryPolicy, FixedBackOffPolicy fixedBackOffPolicy){
        RequestHandlerRetryAdvice retryAdvice = new RequestHandlerRetryAdvice();
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);
        retryAdvice.setRetryTemplate(retryTemplate);
        return retryAdvice;
    }


    @Bean
    public ExpressionEvaluatingRequestHandlerAdvice errorAdvice() {
        ExpressionEvaluatingRequestHandlerAdvice advice = new ExpressionEvaluatingRequestHandlerAdvice();
        advice.setTrapException(true);
        advice.setFailureChannelName("orderItemFailure");
        return advice;
    }

    @Bean
    public IntegrationFlow orderItemFailureFlow() {
        return IntegrationFlows.from("orderItemFailure")
                .handle((payload, headers) -> {
                    OrderItem failedItem = (OrderItem)((MessagingException) payload).getFailedMessage().getPayload();
                    failedItem.setStatus("ERROR");
                    return null;
                }).nullChannel()
                ;
    }
}
