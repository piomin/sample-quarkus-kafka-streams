package pl.piomin.samples.streams.stock.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import pl.piomin.samples.streams.stock.model.Order;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {
}
