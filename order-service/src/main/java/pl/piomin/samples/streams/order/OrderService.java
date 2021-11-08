package pl.piomin.samples.streams.order;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;
import pl.piomin.samples.streams.order.model.Order;
import pl.piomin.samples.streams.order.model.OrderType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@ApplicationScoped
public class OrderService {

    private static final Random random = new Random();

    @Inject
    Logger log;

    long orderId = 100;

    Map<Integer, Integer> prices = Map.of(
            1, 1000,
            2, 2000,
            3, 5000,
            4, 1500,
            5, 2500,
            6, 500,
            7, 1200,
            8, 4300,
            9, 4700,
            10, 500);

    LinkedList<Order> buyOrders = new LinkedList<>(List.of(
            new Order(++orderId, 1, 1, 100, null, OrderType.BUY, 1000),
            new Order(++orderId, 2, 1, 200, null, OrderType.BUY, 1050),
            new Order(++orderId, 3, 1, 100, null, OrderType.BUY, 1030),
            new Order(++orderId, 4, 1, 200, null, OrderType.BUY, 1050),
            new Order(++orderId, 5, 1, 200, null, OrderType.BUY, 1000),
            new Order(++orderId, 11, 1, 100, null, OrderType.BUY, 1050)
    ));

    LinkedList<Order> sellOrders = new LinkedList<>(List.of(
            new Order(++orderId, 6, 1, 200, null, OrderType.SELL, 950),
            new Order(++orderId, 7, 1, 100, null, OrderType.SELL, 1000),
            new Order(++orderId, 8, 1, 100, null, OrderType.SELL, 1050),
            new Order(++orderId, 9, 1, 300, null, OrderType.SELL, 1000),
            new Order(++orderId, 10, 1, 200, null, OrderType.SELL, 1020)
    ));

    @Outgoing("orders-buy")
    public Multi<Record<Long, Order>> buyOrdersGenerator() {
        return Multi.createFrom().ticks().every(Duration.ofMillis(1000))
                .map(order -> {
                    if (buyOrders.peek() != null) {
                        Order o = buyOrders.poll();
                        Record<Long, Order> r = Record.of(o.getId(), o);
                        log.infof("Sent: %s", r.value());
                        return r;
                    } else {
                        return Record.of(0L, new Order());
                    }
                })
                .filter(r -> !r.key().equals(0L));
    }

    @Outgoing("orders-sell")
    public Multi<Record<Long, Order>> sellOrdersGenerator() {
        return Multi.createFrom().ticks().every(Duration.ofMillis(1000))
                .map(order -> {
                    if (sellOrders.peek() != null) {
                        Order o = sellOrders.poll();
                        Record<Long, Order> r = Record.of(o.getId(), o);
                        log.infof("Sent: %s", r.value());
                        return r;
                    } else {
                        return Record.of(0L, new Order());
                    }
                })
                .filter(r -> !r.key().equals(0L));
    }

//    @Outgoing("orders-buy")
//    public Multi<Record<Long, Order>> buyOrdersGenerator() {
//        return Multi.createFrom().ticks().every(Duration.ofMillis(5000))
//                .map(order -> {
//                    Integer productId = random.nextInt(10) + 1;
//                    int price = prices.get(productId) + random.nextInt(200);
//                    Order o = new Order(
//                            ++orderId,
//                            random.nextInt(1000) + 1,
//                            productId,
//                            100 * (random.nextInt(5) + 1),
//                            LocalDateTime.now(),
//                            OrderType.BUY,
//                            price);
//                    log.infof("Sent: %s", o);
//                    return Record.of(o.getId(), o);
//                });
//    }

//    @Outgoing("orders-sell")
//    public Multi<Record<Long, Order>> sellOrdersGenerator() {
//        return Multi.createFrom().ticks().every(Duration.ofMillis(5000))
//                .map(order -> {
//                    Integer productId = random.nextInt(10) + 1;
//                    int price = prices.get(productId) + random.nextInt(200);
//                    Order o = new Order(
//                            ++orderId,
//                            random.nextInt(1000) + 1,
//                            productId,
//                            100 * (random.nextInt(5) + 1),
//                            LocalDateTime.now(),
//                            OrderType.SELL,
//                            price);
//                    log.infof("Sent: %s", o);
//                    return Record.of(o.getId(), o);
//                });
//    }
}
