package message.exeption;

public class FailInputArgumentsException extends BrokenPacketException{
    public FailInputArgumentsException() {
        super();
    }

    public FailInputArgumentsException(String message) {
        super(message);
    }

    public FailInputArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailInputArgumentsException(Throwable cause) {
        super(cause);
    }
}
