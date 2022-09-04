import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Message {
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd.MM.yyyy hh:mm:ss")
            .withZone(ZoneId.systemDefault());
    private final String time;
    private final String name;
    private final String message;

    public Message(String name, String message) {
        this.time = formatter.format(Instant.now());
        this.name = name;
        this.message = message;
    }

    public Message(@JsonProperty("time") String time, @JsonProperty("name") String name,
                   @JsonProperty("message") String message) {
        this.time = time;
        this.name = name;
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}
