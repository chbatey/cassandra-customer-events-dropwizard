package info.batey.eventstore.cassandra;

import com.datastax.driver.core.Cluster;
import rx.Observable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Sandbox {
    public static void main(String[] args) throws Exception {

        Cluster cluster = Cluster.builder().addContactPoint("localhost").build();

        CustomerEventDaoAsync customerEventDao = new CustomerEventDaoAsync(cluster.connect("customers"));
        customerEventDao.prepareStatements();
        OutputStream outputStream = new FileOutputStream("");

        Observable<CustomerEvent> chbatey = customerEventDao.getCustomerEventsObservable("chbatey");

        chbatey.doOnCompleted(() -> { try { outputStream.close(); } catch (IOException e) { } });

        chbatey.subscribe(event -> { try { outputStream.write(event.serialise()); } catch (IOException e) { } });

        Thread.sleep(10000);

        cluster.close();
    }


}
