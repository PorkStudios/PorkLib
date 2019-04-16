package net.daporkchop.lib.binary.buf.exception;

/**
 * @author DaPorkchop_
 */
public class PorkBufCannotExpandException extends PorkBufException {
    public PorkBufCannotExpandException() {
        super();
    }

    public PorkBufCannotExpandException(String message) {
        super(message);
    }

    public PorkBufCannotExpandException(String message, Throwable cause) {
        super(message, cause);
    }

    public PorkBufCannotExpandException(Throwable cause) {
        super(cause);
    }

    protected PorkBufCannotExpandException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
