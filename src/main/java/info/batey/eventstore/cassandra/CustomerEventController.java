package info.batey.eventstore.cassandra;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.List;

@Path("/events")
public class CustomerEventController {

    private final static Logger LOGGER = LoggerFactory.getLogger(CustomerEventController.class);

    private CustomerEventDao customerEventDao;

    public CustomerEventController(CustomerEventDao customerEventDao) {
        this.customerEventDao = customerEventDao;
    }

    @GET
    public List<CustomerEvent> getEvents() {
        return customerEventDao.getAllCustomerEvents();
    }

    @GET
    @Path("/{customerId}")
    public List<CustomerEvent> getEventsForTime(@PathParam("customerId") String customerId,
                                                @QueryParam("startTime") Long startTime,
                                                @QueryParam("endTime") Long endTime) {

        if (startTime != null && endTime != null) {
            LOGGER.info("Getting events from {} to {} for customer {}", startTime, endTime, customerId);
            return customerEventDao.getCustomerEventsForTime(customerId, startTime, endTime);
        } else {
            LOGGER.info("Getting events all or customer {}", customerId);
            return customerEventDao.getCustomerEvents(customerId);
        }
    }

}
