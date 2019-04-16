package net.daporkchop.lib.binary.buf.exception;

/**
 * @author DaPorkchop_
 */
public abstract class PorkBufException extends RuntimeException {
    public PorkBufException() {
        super();
    }

    public PorkBufException(String message) {
        super(message);
    }

    public PorkBufException(String message, Throwable cause) {
        super(message, cause);
    }

    public PorkBufException(Throwable cause) {
        super(cause);
    }

    protected PorkBufException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
