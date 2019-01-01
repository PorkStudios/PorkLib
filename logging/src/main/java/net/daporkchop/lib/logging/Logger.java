/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.logging;

import lombok.*;
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.common.util.Formatter;
import net.daporkchop.lib.common.util.PorkUtil;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author DaPorkchop_
 */
@Getter
@Builder(builderClassName = "Builder")
public class Logger implements Logging {
    private static final String ALERT_HEADER = "****************************************";
    private static final String ALERT_PREFIX = "* ";
    private static final String ALERT_FOOTER = ALERT_HEADER;

    /**
     * A default logger instance. This will print messages from all levels to {@link System#out}.
     */
    public static final Logger DEFAULT_LOG = builder().level(1).build();

    /**
     * The {@link PrintStream} that messages will be written to
     */
    @NonNull
    @lombok.Builder.Default
    private final PrintStream out = System.out;

    /**
     * The level of log messages to be printed.
     * <p>
     * if < 0: nothing at all
     * if   0: only essential messages
     * if   1: essential + warnings + notifications
     * if   2: essential + warnings + notifications + trace
     * if > 2: everything
     */
    @Setter
    @lombok.Builder.Default
    private int level = 3;

    /**
     * An instance of {@link DateFormat} that will be used to format the timestamp that prefixes messages.
     * <p>
     * If {@code null}, the date prefix will be omitted, even if {@link Logger#prefixOrder} would add a date.
     */
    @lombok.Builder.Default
    private final DateFormat dateFormat = new SimpleDateFormat("[dd/MM/yyyy HH:mm:ss] ");

    /**
     * A {@link String} that will be used to format the {@link LogLevel} prefix applied to messages.
     * <p>
     * This is given to {@link String#format(String, Object...)} with a single parameter being the name of the log level.
     * <p>
     * If {@code null}, the log level prefix will be omitted, even if {@link Logger#prefixOrder} would add a log level.
     */
    @lombok.Builder.Default
    private final String levelFormat = "[%s] ";

    /**
     * A collection of {@link OutputStream}s that will also have the message content written to them.
     * <p>
     * This could be useful for e.g. logging to a file (by using a {@link java.io.FileOutputStream})
     */
    private final Set<OutputStream> otherOutputs = new HashSet<>();

    private final Lock lock = new ReentrantLock();

    /**
     * The {@link PrefixOrder} used for prefixing the message.
     */
    @NonNull
    @lombok.Builder.Default
    private final PrefixOrder prefixOrder = PrefixOrder.DATE_LEVEL;

    {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                for (OutputStream out : this.otherOutputs)  {
                    out.close();
                }
            } catch (IOException e)  {
                throw this.exception(e);
            }
        }));
    }

    public void log(@NonNull String message) {
        this.log(message, LogLevel.INFO);
    }

    public void info(@NonNull String message) {
        this.log(message, LogLevel.INFO);
    }

    public void error(@NonNull String message) {
        this.log(message, LogLevel.ERROR);
    }

    public void fatal(@NonNull String message) {
        this.log(message, LogLevel.FATAL);
    }

    public void alert(@NonNull String message) {
        this.log(message, LogLevel.ALERT);
    }

    public void warn(@NonNull String message) {
        this.log(message, LogLevel.WARN);
    }

    public void notify(@NonNull String message) {
        this.log(message, LogLevel.NOTIFY);
    }

    public void trace(@NonNull String message) {
        this.log(message, LogLevel.TRACE);
    }

    public void debug(@NonNull String message) {
        this.log(message, LogLevel.DEBUG);
    }

    public void log(@NonNull String message, @NonNull LogLevel level) {
        if (level.getLevel() <= this.level) {
            if (message.indexOf('\n') == -1) {
                if (level == LogLevel.ALERT)    {
                    this.actuallyDoTheLoggingThing(this.format(ALERT_HEADER, LogLevel.ALERT).getBytes(UTF8.utf8));
                    this.actuallyDoTheLoggingThing(this.format(ALERT_PREFIX, LogLevel.ALERT).getBytes(UTF8.utf8));
                    this.actuallyDoTheLoggingThing(this.format(String.format("%s%s", ALERT_PREFIX, message), level).getBytes(UTF8.utf8));
                    this.actuallyDoTheLoggingThing(this.format(ALERT_PREFIX, LogLevel.ALERT).getBytes(UTF8.utf8));
                    this.actuallyDoTheLoggingThing(this.format(ALERT_FOOTER, LogLevel.ALERT).getBytes(UTF8.utf8));
                    return;
                }
                String msg = this.format(message, level);
                this.actuallyDoTheLoggingThing(msg.getBytes(UTF8.utf8));
            } else {
                this.lock.lock();
                try {
                    if (level == LogLevel.ALERT)    {
                        this.actuallyDoTheLoggingThing(this.format(ALERT_HEADER, LogLevel.ALERT).getBytes(UTF8.utf8));
                        this.actuallyDoTheLoggingThing(this.format(ALERT_PREFIX, LogLevel.ALERT).getBytes(UTF8.utf8));
                        for (String subMsg : message.split("\n")) {
                            this.actuallyDoTheLoggingThing(this.format(String.format("%s%s", ALERT_PREFIX, subMsg), level).getBytes(UTF8.utf8));
                        }
                        this.actuallyDoTheLoggingThing(this.format(ALERT_PREFIX, LogLevel.ALERT).getBytes(UTF8.utf8));
                        this.actuallyDoTheLoggingThing(this.format(ALERT_FOOTER, LogLevel.ALERT).getBytes(UTF8.utf8));
                        return;
                    }
                    for (String subMsg : message.split("\n")) {
                        String msg = this.format(subMsg, level);
                        this.actuallyDoTheLoggingThing(msg.getBytes(UTF8.utf8));
                    }
                } finally {
                    this.lock.unlock();
                }
            }
        }
    }

    public void info(@NonNull String message, @NonNull Object... params) {
        this.log(message, LogLevel.INFO, params);
    }

    public void error(@NonNull String message, @NonNull Object... params) {
        this.log(message, LogLevel.ERROR, params);
    }

    public void fatal(@NonNull String message, @NonNull Object... params) {
        this.log(message, LogLevel.FATAL, params);
    }

    public void alert(@NonNull String message, @NonNull Object... params) {
        this.log(message, LogLevel.ALERT, params);
    }

    public void warn(@NonNull String message, @NonNull Object... params) {
        this.log(message, LogLevel.WARN, params);
    }

    public void notify(@NonNull String message, @NonNull Object... params) {
        this.log(message, LogLevel.NOTIFY, params);
    }

    public void trace(@NonNull String message, @NonNull Object... params) {
        this.log(message, LogLevel.TRACE, params);
    }

    public void debug(@NonNull String message, @NonNull Object... params) {
        this.log(message, LogLevel.DEBUG, params);
    }

    public void log(@NonNull String message, @NonNull LogLevel level, @NonNull Object... params) {
        if (level.getLevel() <= this.level) {
            this.log(Formatter.format(message, params), level);
        }
    }

    public void error(@NonNull Throwable t) {
        this.lock.lock();
        try {
            // Print our stack trace
            this.error(t.toString());
            StackTraceElement[] trace = PorkUtil.getStackTrace(t);
            for (StackTraceElement element : trace) {
                this.error("\tat ${0}", element);
            }

            // Print cause, if any
            Throwable cause = t.getCause();
            if (cause != null) {
                this.error("Caused by:");
                this.error(cause);
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void add(@NonNull File file) {
        this.add(file, false);
    }

    public void add(@NonNull File file, boolean overwrite) {
        try {
            if (file.exists()) {
                if (!file.isFile()) {
                    throw this.exception("Not a file: ${0}", file);
                }
            } else {
                File parent = file.getParentFile();
                if (!parent.exists() && !parent.mkdirs()) {
                    throw this.exception("Couldn't create directory: ${0}", parent);
                } else if (!file.createNewFile()) {
                    throw this.exception("Couldn't create file: ${0}", file);
                }
            }
            this.otherOutputs.add(new BufferedOutputStream(new FileOutputStream(file, !overwrite)));
        } catch (IOException e) {
            throw this.exception("Unable to add log output file: ${0}", e, file);
        }
    }

    protected void actuallyDoTheLoggingThing(@NonNull byte[] toWrite) {
        this.lock.lock();
        try {
            this.out.write(toWrite);
            for (OutputStream out : this.otherOutputs) {
                out.write(toWrite);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to print message", e);
        } finally {
            this.lock.unlock();
        }
    }

    protected String format(@NonNull String message, @NonNull LogLevel level) {
        return this.prefixOrder.getPrefixer().prefix(this.dateFormat, level, this.levelFormat, message);//.endsWith("\n") ? message : String.format("%s\n", message));
    }
}
