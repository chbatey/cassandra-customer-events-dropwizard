package info.batey.eventstore.cassandra;

import com.datastax.driver.core.Cluster;
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
        CustomerEventDao customerEventDao = new CustomerEventDao(cluster.connect());
        CustomerEventController customerEventController = new CustomerEventController(customerEventDao);
        environment.jersey().register(customerEventController);
    }
}
