package info.batey.eventstore.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class CustomerEventsApp extends Application<ApplicationConfiguration> {

    public static void main(String[] args) throws Exception {
        new CustomerEventsApp().run(args);
    }

    @Override
    public void initialize(Bootstrap<ApplicationConfiguration> bootstrap) {

    }

    @Override
    public void run(ApplicationConfiguration applicationConfiguration, Environment environment) throws Exception {
        Cluster cluster = applicationConfiguration.getCassandraFactory().build(environment);
        Session connect = cluster.connect();
        CustomerEventDao customerEventDao = new CustomerEventDao(connect);
        CustomerEventDaoAsync customerEventDaoAsync = new CustomerEventDaoAsync(connect);
        CustomerEventController customerEventController = new CustomerEventController(customerEventDao, customerEventDaoAsync);
        environment.jersey().register(customerEventController);
    }
}
