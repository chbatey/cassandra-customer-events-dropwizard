package info.batey.eventstore.cassandra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOError;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class CustomerEvent {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String customerId;

    private UUID time;

    private String staffId;

    private String storeType;

    private String eventType;

    private Map<String, String> tags;

    public CustomerEvent(String customerId, UUID time, String staffId, String storeType, String eventType, Map<String, String> tags) {
        this.customerId = customerId;
        this.time = time;
        this.staffId = staffId;
        this.storeType = storeType;
        this.eventType = eventType;
        this.tags = tags;
    }

    public String getCustomerId() {
        return customerId;
    }

    public UUID getTime() {
        return time;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getStoreType() {
        return storeType;
    }

    public String getEventType() {
        return eventType;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public byte[] serialise() throws IOException {
        return objectMapper.writeValueAsBytes(this);
    }
}
