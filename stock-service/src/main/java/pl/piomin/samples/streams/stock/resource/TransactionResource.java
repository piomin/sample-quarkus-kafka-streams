package pl.piomin.samples.streams.stock.resource;

import pl.piomin.samples.streams.stock.model.TransactionTotal;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
