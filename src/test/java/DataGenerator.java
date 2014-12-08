import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;

import java.util.HashMap;

public class DataGenerator {
    public static void main(String[] args) {
        Cluster cluster = Cluster.builder().addContactPoint("localhost").build();

        String originalJson = "";

        Session session = cluster.connect("customers");
        PreparedStatement prepared = session.prepare("INSERT INTO customer_events (customer_id, time , event_type , staff_id , store_type , tags ) " +
                "VALUES ( ?,?,?,?,?,?)");

        for ( int i = 0; i < 10000000; i++) {
            BoundStatement bind = prepared.bind("chbatey", UUIDs.timeBased(), "basket_add", "trevor", "online", new HashMap<>());
            session.executeAsync(bind);
        }

        cluster.close();
    }
}
