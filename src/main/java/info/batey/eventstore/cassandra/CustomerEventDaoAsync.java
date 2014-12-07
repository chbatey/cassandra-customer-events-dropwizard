package info.batey.eventstore.cassandra;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

public class CustomerEventDaoAsync {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerEventDaoAsync.class);

    private Session session;

    private PreparedStatement getEventsForCustomer;

    public CustomerEventDaoAsync(Session session) {
        this.session = session;
    }

    @PostConstruct
    public void prepareStatements() {
        getEventsForCustomer = session.prepare("select * from customers.customer_events where customer_id = ?");
    }

    /*
     * Synchronously gets all the events for a single customer
     */
    public List<CustomerEvent> getCustomerEvents(String customerId) {
        BoundStatement boundStatement = getEventsForCustomer.bind(customerId);
        return session.execute(boundStatement).all().stream()
                .map(mapCustomerEvent())
                .collect(Collectors.toList());
    }

    /*
     * Async gets all the events for a single customer + transform
     */
    public ListenableFuture<List<CustomerEvent>> getCustomerEventsAsync(String customerId) {
        BoundStatement boundStatement = getEventsForCustomer.bind(customerId);
        ListenableFuture<ResultSet> resultSetFuture = session.executeAsync(boundStatement);
        ListenableFuture<List<CustomerEvent>> transform = Futures.transform(resultSetFuture, (com.google.common.base.Function<ResultSet, List<CustomerEvent>>)
                queryResult -> queryResult.all().stream().map(mapCustomerEvent()).collect(Collectors.toList()));
        return transform;
    }

    /*
     * Async gets all the events for a single customer + transform
     */
    public Observable<CustomerEvent> getCustomerEventsObservable(String customerId) {
        BoundStatement boundStatement = getEventsForCustomer.bind(customerId);
        ListenableFuture<ResultSet> resultSetFuture = session.executeAsync(boundStatement);
        Observable<ResultSet> observable = Observable.from(resultSetFuture, Schedulers.io());
        Observable<Row> rowObservable = observable.flatMapIterable(result -> result);
        return rowObservable.map(row -> new CustomerEvent(
                row.getString("customer_id"),
                row.getUUID("time"),
                row.getString("staff_id"),
                row.getString("store_type"),
                row.getString("event_type"),
                row.getMap("tags", String.class, String.class)));

    }



    public List<CustomerEvent> getAllCustomerEvents() {
        return session.execute("select * from customers.customer_events")
                .all().stream()
                .map(mapCustomerEvent())
                .collect(Collectors.toList());

    }

    public List<CustomerEvent> getCustomerEventsForTime(String customerId, long startTime, long endTime) {
        Select.Where getCustomers = QueryBuilder.select()
                .all()
                .from("customers", "customer_events")
                .where(eq("customer_id", customerId))
                .and(gt("time", UUIDs.startOf(startTime)))
                .and(lt("time", UUIDs.endOf(endTime)));

        LOGGER.info("Executing {}", getCustomers);

        return session.execute(getCustomers).all().stream()
                .map(mapCustomerEvent())
                .collect(Collectors.toList());
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

    private Function<Row, CustomerEvent> mapCustomerEventToObservable() {
        return row -> new CustomerEvent(
                row.getString("customer_id"),
                row.getUUID("time"),
                row.getString("staff_id"),
                row.getString("store_type"),
                row.getString("event_type"),
                row.getMap("tags", String.class, String.class));
    }
}
