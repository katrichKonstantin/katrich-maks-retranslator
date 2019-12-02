package message.exeption;

import message.exeption.FailInputArgumentsException;

public class IllegalContactIdException extends FailInputArgumentsException {
    public IllegalContactIdException() {
        super();
    }

    public IllegalContactIdException(String message) {
        super(message);
    }

    public IllegalContactIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalContactIdException(Throwable cause) {
        super(cause);
    }
}
