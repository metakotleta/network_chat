import java.nio.channels.SelectionKey;

public class User {

    private String name;
    private SelectionKey userKey;

    public User(String name, SelectionKey userKey) {
        this.name = name;
        this.userKey = userKey;
    }
}
