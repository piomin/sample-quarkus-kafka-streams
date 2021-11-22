package pl.piomin.samples.streams.order.model.deserializer;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import pl.piomin.samples.streams.order.model.Transaction;

public class TransactionDeserializer extends ObjectMapperDeserializer<Transaction> {
    public TransactionDeserializer() {
        super(Transaction.class);
    }
}