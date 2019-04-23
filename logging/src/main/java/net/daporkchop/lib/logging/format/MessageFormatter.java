package net.daporkchop.lib.logging.format;

import lombok.NonNull;
import net.daporkchop.lib.logging.LogLevel;

import java.util.Date;

/**
 * Formats log messages for printing to the console
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface MessageFormatter {
    /**
     * Prepares the actual message for printing
     *
     * @param date        the date that the message was sent at, should be used for message timestamps
     * @param channelName the name of the channel that the message was sent on. May be {@code null}
     * @param level       the log level that the message was sent using
     * @param message     the actual message
     * @return a formatted message, ready to be printed to the console
     */
    String format(@NonNull Date date, String channelName, @NonNull LogLevel level, @NonNull String message);
}
