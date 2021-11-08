package pl.piomin.samples.streams.stock.logic;

import org.hibernate.exception.ConstraintViolationException;
import org.jboss.logging.Logger;
import pl.piomin.samples.streams.stock.model.Order;
import pl.piomin.samples.streams.stock.repository.OrderRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;

@ApplicationScoped
public class OrderLogic {

    @Inject
    Logger log;
    @Inject
    OrderRepository repository;

    @Transactional
    public Order add(Order order) {
        repository.persist(order);
        return order;
    }

    @Transactional
    public boolean performUpdate(Long buyOrderId, Long sellOrderId, int amount) {
        Order buyOrder = repository.findById(buyOrderId, LockModeType.PESSIMISTIC_WRITE);
        Order sellOrder = repository.findById(sellOrderId, LockModeType.PESSIMISTIC_WRITE);
        if (buyOrder == null || sellOrder == null)
            return false;
        int buyAvailableCount = buyOrder.getProductCount() - buyOrder.getRealizedCount();
        int sellAvailableCount = sellOrder.getProductCount() - sellOrder.getRealizedCount();
        if (buyAvailableCount >= amount && sellAvailableCount >= amount) {
            buyOrder.setRealizedCount(buyOrder.getRealizedCount() + amount);
            sellOrder.setRealizedCount(sellOrder.getRealizedCount() + amount);
            repository.persist(buyOrder);
            repository.persist(sellOrder);
            return true;
        } else {
            return false;
        }
    }
}
