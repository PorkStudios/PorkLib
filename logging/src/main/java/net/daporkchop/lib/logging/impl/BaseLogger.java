package net.daporkchop.lib.logging.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.daporkchop.lib.logging.LogLevel;
import net.daporkchop.lib.logging.Logger;
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
public class BaseLogger implements Logger {
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

    @Override
    public void log(@NonNull LogLevel level, @NonNull String message) {
        this.doLog(level, null, message);
    }

    protected synchronized void doLog(@NonNull LogLevel level, String channel, @NonNull String message) {
        Date date = Date.from(Instant.now());
        String[] split = message.trim().split("\n");
        if (level == LogLevel.ALERT) {
            this.printer.accept(this.messageFormatter.format(date, channel, level, this.alertHeader));
            this.printer.accept(this.messageFormatter.format(date, channel, level, this.alertPrefix));
            for (String line : split)   {
                this.printer.accept(this.messageFormatter.format(date, channel, level, this.alertPrefix + line));
            }
            this.printer.accept(this.messageFormatter.format(date, channel, level, this.alertPrefix));
            this.printer.accept(this.messageFormatter.format(date, channel, level, this.alertFooter));
        } else {
            for (String line : split)   {
                this.printer.accept(this.messageFormatter.format(date, channel, level, line));
            }
        }
    }

    @Override
    public Logger channel(@NonNull String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Channel name may not be empty!");
        } else {
            return new Logger() {
                @Override
                public void log(@NonNull LogLevel level, @NonNull String message) {
                    BaseLogger.this.doLog(level, name, message);
                }

                @Override
                public MessageFormatter getMessageFormatter() {
                    return BaseLogger.this.getMessageFormatter();
                }

                @Override
                public void setMessageFormatter(@NonNull MessageFormatter formatter) {
                    BaseLogger.this.setMessageFormatter(formatter);
                }

                @Override
                public String getAlertHeader() {
                    return BaseLogger.this.getAlertHeader();
                }

                @Override
                public void setAlertHeader(@NonNull String alertHeader) {
                    BaseLogger.this.setAlertHeader(alertHeader);
                }

                @Override
                public String getAlertPrefix() {
                    return BaseLogger.this.getAlertPrefix();
                }

                @Override
                public void setAlertPrefix(@NonNull String alertPrefix) {
                    BaseLogger.this.setAlertPrefix(alertPrefix);
                }

                @Override
                public String getAlertFooter() {
                    return BaseLogger.this.getAlertFooter();
                }

                @Override
                public void setAlertFooter(@NonNull String alertFooter) {
                    BaseLogger.this.setAlertFooter(alertFooter);
                }

                @Override
                public Logger channel(@NonNull String name) {
                    return BaseLogger.this.channel(name);
                }
            };
        }
    }
}
