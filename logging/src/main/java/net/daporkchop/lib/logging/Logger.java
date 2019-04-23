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

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.logging.format.MessageFormatter;

import java.io.PrintWriter;
import java.util.StringJoiner;
import java.util.function.Consumer;

/**
 * Utility class to help with writing log messages
 *
 * @author DaPorkchop_
 */
public interface Logger {
    String DEFAULT_ALERT_HEADER = "****************************************";
    String DEFAULT_ALERT_PREFIX = "* ";
    String DEFAULT_ALERT_FOOTER = DEFAULT_ALERT_HEADER;

    //
    //
    // Utility methods
    //
    //

    /**
     * Prints the stack trace of an exception line-by-line, passing each line to a given callback function
     *
     * @param throwable   the exection whose stack trace should be printed
     * @param linePrinter a callback function that will be invoked once for each line
     */
    static void getStackTrace(@NonNull Throwable throwable, @NonNull Consumer<String> linePrinter) {
        throwable.printStackTrace(new PrintWriter(DataOut.slashDevSlashNull(), true) {
            @Override
            public void println(Object x) {
                linePrinter.accept(String.valueOf(x));
            }
        });
    }

    //
    //
    // Actual logging functions
    //
    //

    /**
     * Writes a plain message to the log.
     *
     * @param level   the log level to use
     * @param message the message to be written
     */
    void log(@NonNull LogLevel level, @NonNull String message);

    /**
     * Writes a formatted message to the log.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param level  the log level to use
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default void log(@NonNull LogLevel level, @NonNull String format, @NonNull Object... args) {
        this.log(level, String.format(format, args));
    }

    /**
     * Writes a plain message to the log using the {@link LogLevel#INFO} level.
     *
     * @param message the message to be written
     */
    default void info(@NonNull String message) {
        this.log(LogLevel.INFO, message);
    }

    /**
     * Writes a formatted message to the log using the {@link LogLevel#INFO} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default void info(@NonNull String format, @NonNull Object... args) {
        this.log(LogLevel.INFO, String.format(format, args));
    }

    /**
     * Writes an exception stack trace to the log using the {@link LogLevel#INFO} level.
     *
     * @param throwable the exception whose stack trace should be printed
     */
    default void info(@NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        getStackTrace(throwable, joiner::add);
        this.info(joiner.toString());
    }

    /**
     * Writes a plain message to the log using the {@link LogLevel#ERROR} level.
     *
     * @param message the message to be written
     */
    default void error(@NonNull String message) {
        this.log(LogLevel.ERROR, message);
    }

    /**
     * Writes a formatted message to the log using the {@link LogLevel#ERROR} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default void error(@NonNull String format, @NonNull Object... args) {
        this.log(LogLevel.ERROR, String.format(format, args));
    }

    /**
     * Writes an exception stack trace to the log using the {@link LogLevel#ERROR} level.
     *
     * @param throwable the exception whose stack trace should be printed
     */
    default void error(@NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        getStackTrace(throwable, joiner::add);
        this.error(joiner.toString());
    }

    /**
     * Writes a plain message to the log using the {@link LogLevel#FATAL} level.
     *
     * @param message the message to be written
     */
    default void fatal(@NonNull String message) {
        this.log(LogLevel.FATAL, message);
    }

    /**
     * Writes a formatted message to the log using the {@link LogLevel#FATAL} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default void fatal(@NonNull String format, @NonNull Object... args) {
        this.log(LogLevel.FATAL, String.format(format, args));
    }

    /**
     * Writes an exception stack trace to the log using the {@link LogLevel#FATAL} level.
     *
     * @param throwable the exception whose stack trace should be printed
     */
    default void fatal(@NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        getStackTrace(throwable, joiner::add);
        this.fatal(joiner.toString());
    }

    /**
     * Writes a plain message to the log using the {@link LogLevel#ALERT} level.
     *
     * @param message the message to be written
     */
    default void alert(@NonNull String message) {
        this.log(LogLevel.ALERT, message);
    }

    /**
     * Writes a formatted message to the log using the {@link LogLevel#ALERT} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default void alert(@NonNull String format, @NonNull Object... args) {
        this.log(LogLevel.ALERT, String.format(format, args));
    }

    /**
     * Writes an exception stack trace to the log using the {@link LogLevel#ALERT} level.
     *
     * @param throwable the exception whose stack trace should be printed
     */
    default void alert(@NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        getStackTrace(throwable, joiner::add);
        this.alert(joiner.toString());
    }

    /**
     * Writes a plain message to the log using the {@link LogLevel#WARN} level.
     *
     * @param message the message to be written
     */
    default void warn(@NonNull String message) {
        this.log(LogLevel.WARN, message);
    }

    /**
     * Writes a formatted message to the log using the {@link LogLevel#WARN} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default void warn(@NonNull String format, @NonNull Object... args) {
        this.log(LogLevel.WARN, String.format(format, args));
    }

    /**
     * Writes an exception stack trace to the log using the {@link LogLevel#WARN} level.
     *
     * @param throwable the exception whose stack trace should be printed
     */
    default void warn(@NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        getStackTrace(throwable, joiner::add);
        this.warn(joiner.toString());
    }

    /**
     * Writes a plain message to the log using the {@link LogLevel#NOTIFY} level.
     *
     * @param message the message to be written
     */
    default void notify(@NonNull String message) {
        this.log(LogLevel.NOTIFY, message);
    }

    /**
     * Writes a formatted message to the log using the {@link LogLevel#NOTIFY} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default void notify(@NonNull String format, @NonNull Object... args) {
        this.log(LogLevel.NOTIFY, String.format(format, args));
    }

    /**
     * Writes an exception stack trace to the log using the {@link LogLevel#NOTIFY} level.
     *
     * @param throwable the exception whose stack trace should be printed
     */
    default void notify(@NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        getStackTrace(throwable, joiner::add);
        this.notify(joiner.toString());
    }

    /**
     * Writes a plain message to the log using the {@link LogLevel#TRACE} level.
     *
     * @param message the message to be written
     */
    default void trace(@NonNull String message) {
        this.log(LogLevel.TRACE, message);
    }

    /**
     * Writes a formatted message to the log using the {@link LogLevel#TRACE} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default void trace(@NonNull String format, @NonNull Object... args) {
        this.log(LogLevel.TRACE, String.format(format, args));
    }

    /**
     * Writes an exception stack trace to the log using the {@link LogLevel#TRACE} level.
     *
     * @param throwable the exception whose stack trace should be printed
     */
    default void trace(@NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        getStackTrace(throwable, joiner::add);
        this.trace(joiner.toString());
    }

    /**
     * Writes a plain message to the log using the {@link LogLevel#DEBUG} level.
     *
     * @param message the message to be written
     */
    default void debug(@NonNull String message) {
        this.log(LogLevel.DEBUG, message);
    }

    /**
     * Writes a formatted message to the log using the {@link LogLevel#DEBUG} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default void debug(@NonNull String format, @NonNull Object... args) {
        this.log(LogLevel.DEBUG, String.format(format, args));
    }

    /**
     * Writes an exception stack trace to the log using the {@link LogLevel#DEBUG} level.
     *
     * @param throwable the exception whose stack trace should be printed
     */
    default void debug(@NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        getStackTrace(throwable, joiner::add);
        this.debug(joiner.toString());
    }

    //
    //
    // Other stuff
    //
    //

    /**
     * Gets the currently used {@link MessageFormatter} for formatting log messages for printing.
     *
     * @return the currently used {@link MessageFormatter}
     */
    MessageFormatter getMessageFormatter();

    /**
     * Gets the currently used {@link MessageFormatter} for formatting log messages for printing.
     *
     * @param formatter the new {@link MessageFormatter} to use
     */
    void setMessageFormatter(@NonNull MessageFormatter formatter);

    /**
     * Gets the currently used header above messages printed with the {@link LogLevel#ALERT} level.
     * <p>
     * Defaults to {@link #DEFAULT_ALERT_HEADER}.
     *
     * @return the current alert header
     */
    String getAlertHeader();

    /**
     * Sets the currently used header above messages printed with the {@link LogLevel#ALERT} level.
     * <p>
     * Defaults to {@link #DEFAULT_ALERT_HEADER}.
     *
     * @param alertHeader the new alert header to use
     */
    void setAlertHeader(@NonNull String alertHeader);

    /**
     * Gets the currently used prefix above messages printed with the {@link LogLevel#ALERT} level.
     * <p>
     * Defaults to {@link #DEFAULT_ALERT_PREFIX}.
     *
     * @return the current alert prefix
     */
    String getAlertPrefix();

    /**
     * Sets the currently used prefix above messages printed with the {@link LogLevel#ALERT} level.
     * <p>
     * Defaults to {@link #DEFAULT_ALERT_PREFIX}.
     *
     * @param alertPrefix the new alert prefix to use
     */
    void setAlertPrefix(@NonNull String alertPrefix);

    /**
     * Gets the currently used footer above messages printed with the {@link LogLevel#ALERT} level.
     * <p>
     * Defaults to {@link #DEFAULT_ALERT_FOOTER}.
     *
     * @return the current alert footer
     */
    String getAlertFooter();

    /**
     * Sets the currently used footer above messages printed with the {@link LogLevel#ALERT} level.
     * <p>
     * Defaults to {@link #DEFAULT_ALERT_FOOTER}.
     *
     * @param alertFooter the new alert footer to use
     */
    void setAlertFooter(@NonNull String alertFooter);
}
