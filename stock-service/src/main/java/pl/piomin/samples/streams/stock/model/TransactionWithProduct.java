package pl.piomin.samples.streams.stock.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TransactionWithProduct {
    private Transaction transaction;
    private Integer productId;

    public TransactionWithProduct() {
    }

    public TransactionWithProduct(Transaction transaction, Integer productId) {
        this.transaction = transaction;
        this.productId = productId;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}
