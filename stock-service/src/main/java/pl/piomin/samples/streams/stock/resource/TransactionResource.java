package pl.piomin.samples.streams.stock.resource;

import pl.piomin.samples.streams.stock.model.TransactionTotal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import java.util.Map;

@ApplicationScoped
@Path("/transactions")
public class TransactionResource {

    @Inject
    InteractiveQueries interactiveQueries;

    @GET
    @Path("/summary")
    public TransactionTotal getTotal() {
        return interactiveQueries.getTransactionsTotalData();
    }

    @GET
    @Path("/products/{id}")
    public TransactionTotal getByProductId(@PathParam("id") Integer productId) {
        return interactiveQueries.getTransactionsPerProductData(productId);
    }

    @GET
    @Path("/products")
    public Map<Integer, TransactionTotal> getAllPerProductId() {
        return interactiveQueries.getAllTransactionsPerProductData();
    }

    @GET
    @Path("/products/{id}/latest")
    public TransactionTotal getByProductIdLatest(@PathParam("id") Integer productId) {
        return interactiveQueries.getAllLatestTransactionsPerProductData().get(productId);
    }

    @GET
    @Path("/products-latest")
    public Map<Integer, TransactionTotal> getAllLatestByProductIdLatest() {
        return interactiveQueries.getAllLatestTransactionsPerProductData();
    }
}
