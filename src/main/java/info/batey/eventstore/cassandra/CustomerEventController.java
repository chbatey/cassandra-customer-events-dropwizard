package info.batey.eventstore.cassandra;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Observer;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Path("/events")
public class CustomerEventController {

    private final static Logger LOGGER = LoggerFactory.getLogger(CustomerEventController.class);

    private CustomerEventDao customerEventDao;

    private CustomerEventDaoAsync customerEventDaoAsync;

    public CustomerEventController(CustomerEventDao customerEventDao, CustomerEventDaoAsync customerEventDaoAsync) {
        this.customerEventDao = customerEventDao;
        this.customerEventDaoAsync = customerEventDaoAsync;
    }

    @GET
    @Path("/stream")
    public Response streamEvents() {
        final StreamingOutput streamingOutput = outputStream -> {
            final Observable<CustomerEvent> allEvents = customerEventDaoAsync.getCustomerEventsObservable();
            final CountDownLatch countDownLatch = new CountDownLatch(1);

            LOGGER.info("Given output stream: " + outputStream);

            allEvents.subscribe(new Observer<CustomerEvent>() {
                @Override
                public void onCompleted() {
                    try {
                        LOGGER.info("Finished writing out events");
                        outputStream.close();
                    } catch (IOException e) {
                        LOGGER.warn("Exception closing output stream", e);
                    } finally {
                        countDownLatch.countDown();
                    }
                }
                @Override
                public void onError(Throwable e) {
                    LOGGER.error("Error streaming events");
                }
                @Override
                public void onNext(CustomerEvent customerEvent) {
                    try {
                        outputStream.write(customerEvent.serialise());
                    } catch (IOException e) {
                        LOGGER.warn("Exception ", e);
                    }
                }
            });

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                LOGGER.warn("Current thread interrupted, resetting flag");
                Thread.currentThread().interrupt();
            }
        };

        return Response.ok().entity(streamingOutput).build();
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
