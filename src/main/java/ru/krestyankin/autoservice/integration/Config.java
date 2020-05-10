package ru.krestyankin.autoservice.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.splitter.AbstractMessageSplitter;
import org.springframework.messaging.Message;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.krestyankin.autoservice.models.Order;
import ru.krestyankin.autoservice.models.SparePart;

@Configuration
public class Config {
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
    public IntegrationFlow autoserviceFlow(ThreadPoolTaskExecutor threadPoolTaskExecutor) {

        return IntegrationFlows.from("ordersInputChannel")
                .split()
                .channel(MessageChannels.executor(threadPoolTaskExecutor))
                .publishSubscribeChannel(orderSubs -> orderSubs.subscribe(
                        orderFlow -> orderFlow.split(new OrderSplitter())
                                .<Object, Boolean>route(p -> p instanceof SparePart, m -> m
                                        .subFlowMapping(true, sf->sf
                                                .handle("partService", "book"))
                                        .subFlowMapping(false, sf->sf
                                                .handle("servService", "book"))
                                )
                        )
                )
                .handle("orderService", "calcTotal")
                .aggregate()
                .get();
    }
}
