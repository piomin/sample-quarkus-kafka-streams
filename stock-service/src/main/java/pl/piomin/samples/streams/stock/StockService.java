package pl.piomin.samples.streams.stock;

import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowBytesStoreSupplier;
import org.jboss.logging.Logger;
import pl.piomin.samples.streams.stock.logic.OrderLogic;
import pl.piomin.samples.streams.stock.model.Order;
import pl.piomin.samples.streams.stock.model.Transaction;
import pl.piomin.samples.streams.stock.model.TransactionTotal;
import pl.piomin.samples.streams.stock.model.TransactionWithProduct;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDateTime;

@ApplicationScoped
public class StockService {

    private static long transactionId = 0;

    public static final String TRANSACTIONS_ALL_SUMMARY = "transactions-all-summary";
    public static final String TRANSACTIONS_PER_PRODUCT_SUMMARY = "transactions-per-product-summary";
    public static final String TRANSACTIONS_PER_PRODUCT_SUMMARY_30S = "transactions-per-product-summary-30s";

    private static final String ORDERS_BUY_TOPIC = "orders.buy";
    private static final String ORDERS_SELL_TOPIC = "orders.sell";
    private static final String TRANSACTIONS_TOPIC = "transactions";
    private static final String TRANSACTIONS_AGGREGATED_TOPIC = "transactions-aggregated";
    private static final String TRANSACTIONS_PER_PRODUCT_AGGREGATED_TOPIC = "transactions-per-product-aggregated";

    @Inject
    Logger log;
    @Inject
    OrderLogic logic;

    @Produces
    public Topology buildTopology() {
        ObjectMapperSerde<Order> orderSerde = new ObjectMapperSerde<>(Order.class);
        ObjectMapperSerde<Transaction> transactionSerde = new ObjectMapperSerde<>(Transaction.class);
        ObjectMapperSerde<TransactionWithProduct> transactionWithProductSerde = new ObjectMapperSerde<>(TransactionWithProduct.class);
        ObjectMapperSerde<TransactionTotal> transactionTotalSerde = new ObjectMapperSerde<>(TransactionTotal.class);

        KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(
                TRANSACTIONS_ALL_SUMMARY);
        KeyValueBytesStoreSupplier storePerProductSupplier = Stores.persistentKeyValueStore(
                TRANSACTIONS_PER_PRODUCT_SUMMARY);
        WindowBytesStoreSupplier windowStoreSupplier = Stores.persistentWindowStore(
                TRANSACTIONS_PER_PRODUCT_SUMMARY_30S, Duration.ofSeconds(30), Duration.ofSeconds(30), false);

        StreamsBuilder builder = new StreamsBuilder();

        KStream<Long, Order> orders = builder.stream(
                ORDERS_SELL_TOPIC,
                Consumed.with(Serdes.Long(), orderSerde));

        builder.stream(ORDERS_BUY_TOPIC, Consumed.with(Serdes.Long(), orderSerde))
                .merge(orders)
                .peek((k, v) -> {
                    log.infof("New: %s", v);
                    logic.add(v);
                });

        builder.stream(ORDERS_BUY_TOPIC, Consumed.with(Serdes.Long(), orderSerde))
                .selectKey((k, v) -> v.getProductId())
                .join(orders.selectKey((k, v) -> v.getProductId()),
                        this::execute,
                        JoinWindows.of(Duration.ofSeconds(10)),
                        StreamJoined.with(Serdes.Integer(), orderSerde, orderSerde))
                .filterNot((k, v) -> v == null)
                .map((k, v) -> new KeyValue<>(v.getId(), v))
                .peek((k, v) -> log.infof("Done -> %s", v))
                .to(TRANSACTIONS_TOPIC, Produced.with(Serdes.Long(), transactionSerde));

        builder.stream(TRANSACTIONS_TOPIC, Consumed.with(Serdes.Long(), transactionSerde))
                .groupBy((k, v) -> v.getStatus(), Grouped.with(Serdes.String(), transactionSerde))
                .aggregate(
                        TransactionTotal::new,
                        (k, v, a) -> {
                            a.setCount(a.getCount() + 1);
                            a.setProductCount(a.getAmount() + v.getAmount());
                            a.setAmount(a.getProductCount() +
                                    (v.getAmount() * v.getPrice()));
                            return a;
                        },
                        Materialized.<String, TransactionTotal> as(storeSupplier)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(transactionTotalSerde))
                .toStream()
                .peek((k, v) -> log.infof("Total: %s", v))
                .to(TRANSACTIONS_AGGREGATED_TOPIC, Produced.with(Serdes.String(), transactionTotalSerde));

        builder.stream(TRANSACTIONS_TOPIC, Consumed.with(Serdes.Long(), transactionSerde))
                .selectKey((k, v) -> v.getSellOrderId())
                .join(orders.selectKey((k, v) -> v.getId()),
                        (t, o) -> new TransactionWithProduct(t, o.getProductId()),
                        JoinWindows.of(Duration.ofSeconds(10)),
                        StreamJoined.with(Serdes.Long(), transactionSerde, orderSerde))
                .groupBy((k, v) -> v.getProductId(), Grouped.with(Serdes.Integer(), transactionWithProductSerde))
                .aggregate(
                        TransactionTotal::new,
                        (k, v, a) -> {
                            a.setCount(a.getCount() + 1);
                            a.setProductCount(a.getAmount() + v.getTransaction().getAmount());
                            a.setAmount(a.getProductCount() +
                                    (v.getTransaction().getAmount() * v.getTransaction().getPrice()));
                            return a;
                        },
                        Materialized.<Integer, TransactionTotal> as(storePerProductSupplier)
                                .withKeySerde(Serdes.Integer())
                                .withValueSerde(transactionTotalSerde))
                .toStream()
                .peek((k, v) -> log.infof("Total per product(%d): %s", k, v))
                .to(TRANSACTIONS_PER_PRODUCT_AGGREGATED_TOPIC, Produced.with(Serdes.Integer(), transactionTotalSerde));

        builder.stream(TRANSACTIONS_TOPIC, Consumed.with(Serdes.Long(), transactionSerde))
                .selectKey((k, v) -> v.getSellOrderId())
                .join(orders.selectKey((k, v) -> v.getId()),
                        (t, o) -> new TransactionWithProduct(t, o.getProductId()),
                        JoinWindows.of(Duration.ofSeconds(30)),
                        StreamJoined.with(Serdes.Long(), transactionSerde, orderSerde))
                .groupBy((k, v) -> v.getProductId(), Grouped.with(Serdes.Integer(), transactionWithProductSerde))
                .windowedBy(TimeWindows.of(Duration.ofSeconds(30)))
                .aggregate(
                        TransactionTotal::new,
                        (k, v, a) -> {
                            a.setCount(a.getCount() + 1);
                            a.setProductCount(a.getAmount() + v.getTransaction().getAmount());
                            a.setAmount(a.getProductCount() +
                                    (v.getTransaction().getAmount() * v.getTransaction().getPrice()));
                            return a;
                        },
                        Materialized.<Integer, TransactionTotal> as(windowStoreSupplier)
                                .withKeySerde(Serdes.Integer())
                                .withValueSerde(transactionTotalSerde))
                .toStream()
                .peek((k, v) -> log.infof("Total windowed(%d): %s", k.key(), v));

        return builder.build();
    }

    private Transaction execute(Order orderBuy, Order orderSell) {
        if (orderBuy.getAmount() >= orderSell.getAmount()) {
            int count = Math.min(orderBuy.getProductCount(), orderSell.getProductCount());
//            log.info("Executed: orderBuy={}, orderSell={}", orderBuy.getId(), orderSell.getId());
            boolean allowed = logic.performUpdate(orderBuy.getId(), orderSell.getId(), count);
            if (!allowed)
                return null;
            else
                return new Transaction(
                        ++transactionId,
                        orderBuy.getId(),
                        orderSell.getId(),
                        count,
                        (orderBuy.getAmount() + orderSell.getAmount()) / 2,
                        LocalDateTime.now(),
                        "NEW"
                );
        } else {
            return null;
        }
    }

}
