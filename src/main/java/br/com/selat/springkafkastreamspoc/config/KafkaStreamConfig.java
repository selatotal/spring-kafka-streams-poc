package br.com.selat.springkafkastreamspoc.config;

import br.com.selat.springkafkastreamspoc.model.*;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.String.format;

@Configuration
public class KafkaStreamConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaStreamConfig.class);
    private CountDownLatch latch = new CountDownLatch(2000);

    @Bean
    public Consumer<KStream<String, String>> process() {
        return input -> input.foreach((key, value) -> {
            logger.info(format("Key: %s, Value: %s", key, value));
        });
    }

    @Bean
    public Function<KStream<Object, String>, KStream<String, WordCount>> wordCount() {
        return input -> input
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                .map((key, value) -> new KeyValue<>(value, value))
                .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
                .windowedBy(TimeWindows.of(5000))
                .count(Materialized.as("wordcount-store"))
                .toStream()
                .map((key, value) -> new KeyValue<>(key.key(), new WordCount(key.key(), value, new Date(key.window().start()), new Date(key.window().end()))));
    }

    @Bean
    public Function<KStream<Object, String>, KStream<?, WordCount>[]> wordCount2() {
        Predicate<Object, WordCount> isEnglish = (k, v) -> v.word.equals("english");
        Predicate<Object, WordCount> isFrench = (k, v) -> v.word.equals("french");
        Predicate<Object, WordCount> isSpanish = (k, v) -> v.word.equals("spanish");
        return input -> input
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                .groupBy((key, value) -> value)
                .windowedBy(TimeWindows.of(5000))
                .count(Materialized.as("WordCounts-branch"))
                .toStream()
                .map(((key, value) -> new KeyValue<>(null, new WordCount(key.key(), value, new Date(key.window().start()), new Date(key.window().end())))))
                .branch(isEnglish, isFrench, isSpanish);
    }

    @Bean
    public BiFunction<KStream<String, Long>, KTable<String, String>, KStream<String, Long>> process2() {
        return (userClicksStream, userRegionsTable) -> (userClicksStream
                .leftJoin(userRegionsTable, (clicks, region) -> new RegionWithClicks(region == null ? "UNKOWN" : region, clicks),
                        Joined.with(Serdes.String(), Serdes.Long(), null))
                .map((user, regionWithClicks) -> new KeyValue<>(regionWithClicks.getRegion(), regionWithClicks.getClicks()))
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
                .reduce(Long::sum)
                .toStream()
        );
    }

    @Bean
    public BiConsumer<KStream<String, Long>, KTable<String, String>> process3() {
        return (userClicksStream, userRegionsTable) -> {
            userClicksStream.foreach(((key, value) -> latch.countDown()));
            userRegionsTable.toStream().foreach(((key, value) -> latch.countDown()));
        };
    }

    @Bean
    public Function<KStream<Long, Order>,
            Function<GlobalKTable<Long, Customer>,
                    Function<GlobalKTable<Long, Product>, KStream<Long, EnrichedOrder>>>> processOrders() {
        return orderStream -> (
                customers -> (
                        products -> (
                                orderStream.join(customers,
                                        (orderId, order) -> order.getCustomerId(),
                                        (order, customer) -> new CustomerOrder(customer, order))
                                        .join(products,
                                                (orderId, customerOrder) -> customerOrder.productId(),
                                                (customerOrder, product) -> {
                                                    EnrichedOrder enrichedOrder = new EnrichedOrder();
                                                    enrichedOrder.setProduct(product);
                                                    enrichedOrder.setCustomer(customerOrder.getCustomer());
                                                    enrichedOrder.setOrder(customerOrder.getOrder());
                                                    return enrichedOrder;
                                                }
                                        )
                        )
                )
        );

    }
}
