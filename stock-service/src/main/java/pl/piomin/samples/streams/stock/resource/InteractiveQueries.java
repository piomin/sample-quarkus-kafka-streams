package pl.piomin.samples.streams.stock.resource;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import pl.piomin.samples.streams.stock.StockService;
import pl.piomin.samples.streams.stock.model.TransactionTotal;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class InteractiveQueries {

    @Inject
    KafkaStreams streams;

    public TransactionTotal getTransactionsTotalData() {
        return getTransactionsTotalStore().get("NEW");
    }

    public TransactionTotal getTransactionsPerProductData(Integer productId) {
        return getTransactionsPerProductStore().get(productId);
    }

    public Map<Integer, TransactionTotal> getAllLatestTransactionsPerProductData() {
        Map<Integer, TransactionTotal> m = new HashMap<>();
        KeyValueIterator<Windowed<Integer>, TransactionTotal> it = getTransactionsPerProductStore30s().all();
        while (it.hasNext()) {
            KeyValue<Windowed<Integer>, TransactionTotal> kv = it.next();
            m.put(kv.key.key(), kv.value);
        }
        return m;
    }

    public Map<Integer, TransactionTotal> getAllTransactionsPerProductData() {
        Map<Integer, TransactionTotal> m = new HashMap<>();
        KeyValueIterator<Integer, TransactionTotal> it = getTransactionsPerProductStore().all();
        while (it.hasNext()) {
            KeyValue<Integer, TransactionTotal> kv = it.next();
            m.put(kv.key, kv.value);
        }
        return m;
    }

    private ReadOnlyKeyValueStore<String, TransactionTotal> getTransactionsTotalStore() {
        return streams.store(
                StoreQueryParameters
                        .fromNameAndType(StockService.TRANSACTIONS_ALL_SUMMARY, QueryableStoreTypes.keyValueStore()));
    }

    private ReadOnlyKeyValueStore<Integer, TransactionTotal> getTransactionsPerProductStore() {
        return streams.store(
                StoreQueryParameters
                        .fromNameAndType(StockService.TRANSACTIONS_PER_PRODUCT_SUMMARY, QueryableStoreTypes.keyValueStore()));
    }

    private ReadOnlyWindowStore<Integer, TransactionTotal> getTransactionsPerProductStore30s() {
        return streams.store(
                StoreQueryParameters
                        .fromNameAndType(StockService.TRANSACTIONS_PER_PRODUCT_SUMMARY_30S, QueryableStoreTypes.windowStore()));
    }
}
