package net.daporkchop.lib.logging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.daporkchop.lib.logging.format.DefaultMessageFormatter;
import net.daporkchop.lib.logging.format.MessageFormatter;

import java.util.Date;
import java.time.Instant;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class DefaultLogger implements Logger {
    @NonNull
    protected final Consumer<String> printer;

    @NonNull
    protected MessageFormatter messageFormatter = new DefaultMessageFormatter();
    @NonNull
    protected String alertHeader = DEFAULT_ALERT_HEADER;
    @NonNull
    protected String alertPrefix = DEFAULT_ALERT_PREFIX;
    @NonNull
    protected String alertFooter = DEFAULT_ALERT_FOOTER;

    public DefaultLogger() {
        this(System.out::println);
    }

    @Override
    public synchronized void log(@NonNull LogLevel level, @NonNull String message) {
        Date date = Date.from(Instant.now());
        String[] split = message.trim().split("\n");
        //TODO: channels
        if (level == LogLevel.ALERT) {
            this.printer.accept(this.messageFormatter.format(date, null, level, this.alertHeader));
            this.printer.accept(this.messageFormatter.format(date, null, level, this.alertPrefix));
            for (String line : split)   {
                this.printer.accept(this.messageFormatter.format(date, null, level, this.alertPrefix + line));
            }
            this.printer.accept(this.messageFormatter.format(date, null, level, this.alertPrefix));
            this.printer.accept(this.messageFormatter.format(date, null, level, this.alertFooter));
        } else {
            for (String line : split)   {
                this.printer.accept(this.messageFormatter.format(date, null, level, line));
            }
        }
    }

    @Override
    public synchronized void alert(@NonNull Throwable throwable) {
        Date date = Date.from(Instant.now());
        this.printer.accept(this.messageFormatter.format(date, null, LogLevel.ALERT, this.alertHeader));
        this.printer.accept(this.messageFormatter.format(date, null, LogLevel.ALERT, this.alertPrefix));
        Logger.getStackTrace(throwable, line -> this.printer.accept(this.messageFormatter.format(date, null, LogLevel.ALERT, this.alertPrefix + line)));
        this.printer.accept(this.messageFormatter.format(date, null, LogLevel.ALERT, this.alertPrefix));
        this.printer.accept(this.messageFormatter.format(date, null, LogLevel.ALERT, this.alertFooter));
    }
}
