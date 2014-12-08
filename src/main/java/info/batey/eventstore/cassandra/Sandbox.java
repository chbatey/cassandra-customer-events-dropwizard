package info.batey.eventstore.cassandra;

import com.datastax.driver.core.Cluster;
import com.google.common.collect.Lists;
import rx.Observable;
import rx.Observer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class Sandbox {
    public static void main(String[] args) throws Exception {

        List<String> strings = Lists.newArrayList("one", "two");

        Observable<String> from = Observable.from(strings);

        from.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                System.out.printf("completed");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                System.out.printf(s);
            }
        });

//        from.doOnTerminate( () -> System.out.printf("Complete"));

        Thread.sleep(10000);

    }

    private static void cassandraAsync() throws FileNotFoundException, InterruptedException {
        Cluster cluster = Cluster.builder().addContactPoint("localhost").build();

        CustomerEventDaoAsync customerEventDao = new CustomerEventDaoAsync(cluster.connect("customers"));

        OutputStream outputStream = new FileOutputStream("");

        Observable<CustomerEvent> chbatey = customerEventDao.getCustomerEventsObservable("chbatey");

        chbatey.doOnCompleted(() -> { try { outputStream.close(); } catch (IOException e) { } });

        chbatey.subscribe(event -> { try { outputStream.write(event.serialise()); } catch (IOException e) { } });

        Thread.sleep(10000);

        cluster.close();
    }


}
