package info.batey.eventstore.cassandra;

import com.datastax.driver.core.*;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomerEventDaoAsync {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerEventDaoAsync.class);

    private Session session;

    private PreparedStatement getEventsForCustomer;

    public CustomerEventDaoAsync(Session session) {
        this.session = session;
        this.getEventsForCustomer = session.prepare("select * from customers.customer_events where customer_id = ?");
    }

    /*
     * Async gets all the events for a single customer + transform
     */
    public ListenableFuture<List<CustomerEvent>> getCustomerEventsAsync(String customerId) {
        BoundStatement boundStatement = getEventsForCustomer.bind(customerId);
        ListenableFuture<ResultSet> resultSetFuture = session.executeAsync(boundStatement);
        return Futures.transform(resultSetFuture,
                (com.google.common.base.Function<ResultSet, List<CustomerEvent>>)
                queryResult -> queryResult.all().stream().map(mapCustomerEvent()).collect(Collectors.toList()));
    }

    /*
     * Async gets all the events for a single customer + transform
     */
    public Observable<CustomerEvent> getCustomerEventsObservable(String customerId) {
        BoundStatement boundStatement = getEventsForCustomer.bind(customerId);
        ListenableFuture<ResultSet> resultSetFuture = session.executeAsync(boundStatement);
        Observable<ResultSet> observable = Observable.from(resultSetFuture, Schedulers.io());
        Observable<Row> rowObservable = observable.flatMapIterable(result -> result);
        return rowObservable.map(mapCustomersInObservable());

    }

    public Observable<CustomerEvent> getCustomerEventsObservable() {
        ResultSetFuture resultSetFuture = session.executeAsync("select * from customers.customer_events");
        Observable<ResultSet> observable = Observable.from(resultSetFuture, Schedulers.io());
        Observable<Row> rowObservable = observable.flatMapIterable(result -> result);
        return rowObservable.map(mapCustomersInObservable());

    }

    private Func1<Row, CustomerEvent> mapCustomersInObservable() {
        return row -> new CustomerEvent(
                row.getString("customer_id"),
                row.getUUID("time"),
                row.getString("staff_id"),
                row.getString("store_type"),
                row.getString("event_type"),
                row.getMap("tags", String.class, String.class));
    }

    private Function<Row, CustomerEvent> mapCustomerEvent() {
        return row -> new CustomerEvent(
                row.getString("customer_id"),
                row.getUUID("time"),
                row.getString("staff_id"),
                row.getString("store_type"),
                row.getString("event_type"),
                row.getMap("tags", String.class, String.class));
    }
}
