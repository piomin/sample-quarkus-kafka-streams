package pl.piomin.samples.streams.stock.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TransactionTotal {
    private int count;
    private int amount;
    private int productCount;

    public TransactionTotal() {
    }

    public TransactionTotal(int count, int amount, int productCount) {
        this.count = count;
        this.amount = amount;
        this.productCount = productCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    @Override
    public String toString() {
        return "TransactionTotal{" +
                "count=" + count +
                ", amount=" + amount +
                ", productCount=" + productCount +
                '}';
    }
}
