package message.exeption;

public class FailTakeException extends FailInputArgumentsException {
    public FailTakeException() {
        super();
    }

    public FailTakeException(String message) {
        super(message);
    }

    public FailTakeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailTakeException(Throwable cause) {
        super(cause);
    }
}
