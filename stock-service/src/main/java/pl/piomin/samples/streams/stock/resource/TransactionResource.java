package pl.piomin.samples.streams.stock.resource;

import pl.piomin.samples.streams.stock.model.TransactionTotal;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

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
    @Path("/product/{id}")
    public TransactionTotal getByProductId(@PathParam("id") Integer productId) {
        return interactiveQueries.getTransactionsPerProductData(productId);
    }

}
