package net.daporkchop.lib.binary.buf.exception;

/**
 * @author DaPorkchop_
 */
public class PorkBufReadOutOfBoundsException extends PorkBufException {
    public PorkBufReadOutOfBoundsException() {
        super();
    }

    public PorkBufReadOutOfBoundsException(String message) {
        super(message);
    }

    public PorkBufReadOutOfBoundsException(String message, Throwable cause) {
        super(message, cause);
    }

    public PorkBufReadOutOfBoundsException(Throwable cause) {
        super(cause);
    }

    protected PorkBufReadOutOfBoundsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
