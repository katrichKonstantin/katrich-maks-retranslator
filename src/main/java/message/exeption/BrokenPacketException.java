package message.exeption;

import java.io.IOException;

public class BrokenPacketException extends IOException {
    public BrokenPacketException() {
        super();
    }

    public BrokenPacketException(String message) {
        super(message);
    }

    public BrokenPacketException(String message, Throwable cause) {
        super(message, cause);
    }

    public BrokenPacketException(Throwable cause) {
        super(cause);
    }
}
