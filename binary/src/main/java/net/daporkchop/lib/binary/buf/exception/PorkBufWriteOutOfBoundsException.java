package net.daporkchop.lib.binary.buf.exception;

/**
 * @author DaPorkchop_
 */
public class PorkBufWriteOutOfBoundsException extends PorkBufException {
    public PorkBufWriteOutOfBoundsException() {
        super();
    }

    public PorkBufWriteOutOfBoundsException(String message) {
        super(message);
    }

    public PorkBufWriteOutOfBoundsException(String message, Throwable cause) {
        super(message, cause);
    }

    public PorkBufWriteOutOfBoundsException(Throwable cause) {
        super(cause);
    }

    protected PorkBufWriteOutOfBoundsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
