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
import net.daporkchop.lib.binary.io.OldDataOut;
import net.daporkchop.lib.logging.format.FormatParser;
import net.daporkchop.lib.logging.format.MessageFormatter;
import net.daporkchop.lib.logging.format.MessagePrinter;
import net.daporkchop.lib.logging.format.TextStyle;
import net.daporkchop.lib.logging.format.component.TextComponent;
import net.daporkchop.lib.logging.format.component.TextComponentString;

import java.awt.Color;
import java.io.PrintWriter;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;

/**
 * Utility class to help with writing log messages
 *
 * @author DaPorkchop_
 */
public interface Logger {
    TextComponent DEFAULT_ALERT_HEADER = new TextComponentString(Color.RED, null, TextStyle.BOLD, "****************************************");
    TextComponent DEFAULT_ALERT_PREFIX = new TextComponentString(Color.RED, null, TextStyle.BOLD, "* ");
    TextComponent DEFAULT_ALERT_FOOTER = DEFAULT_ALERT_HEADER;

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
        throwable.printStackTrace(new PrintWriter(OldDataOut.slashDevSlashNull(), true) {
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
    Logger log(@NonNull LogLevel level, @NonNull String message);

    /**
     * Writes a formatted message to the log.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param level  the log level to use
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default Logger log(@NonNull LogLevel level, @NonNull String format, @NonNull Object... args) {
        return this.log(level, String.format(format, args));
    }

    /**
     * Writes a plain message to the log using the {@link LogLevel#INFO} level.
     *
     * @param message the message to be written
     */
    default Logger info(@NonNull String message) {
        return this.log(LogLevel.INFO, message);
    }

    /**
     * Writes a formatted message to the log using the {@link LogLevel#INFO} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default Logger info(@NonNull String format, @NonNull Object... args) {
        return this.log(LogLevel.INFO, String.format(format, args));
    }

    /**
     * Writes an exception stack trace to the log using the {@link LogLevel#INFO} level.
     *
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger info(@NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        getStackTrace(throwable, joiner::add);
        return this.info(joiner.toString());
    }

    /**
     * Writes a plain message to the log using the {@link LogLevel#SUCCESS} level.
     *
     * @param message the message to be written
     */
    default Logger success(@NonNull String message) {
        return this.log(LogLevel.SUCCESS, message);
    }

    /**
     * Writes a formatted message to the log using the {@link LogLevel#SUCCESS} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default Logger success(@NonNull String format, @NonNull Object... args) {
        return this.log(LogLevel.SUCCESS, String.format(format, args));
    }

    /**
     * Writes an exception stack trace to the log using the {@link LogLevel#SUCCESS} level.
     *
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger success(@NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        getStackTrace(throwable, joiner::add);
        return this.success(joiner.toString());
    }

    /**
     * Writes a plain message to the log using the {@link LogLevel#ERROR} level.
     *
     * @param message the message to be written
     */
    default Logger error(@NonNull String message) {
        return this.log(LogLevel.ERROR, message);
    }

    /**
     * Writes a formatted message to the log using the {@link LogLevel#ERROR} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default Logger error(@NonNull String format, @NonNull Object... args) {
        return this.log(LogLevel.ERROR, String.format(format, args));
    }

    /**
     * Writes an exception stack trace to the log using the {@link LogLevel#ERROR} level.
     *
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger error(@NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        getStackTrace(throwable, joiner::add);
        return this.error(joiner.toString());
    }

    /**
     * Writes a plain message to the log using the {@link LogLevel#FATAL} level.
     *
     * @param message the message to be written
     */
    default Logger fatal(@NonNull String message) {
        return this.log(LogLevel.FATAL, message);
    }

    /**
     * Writes a formatted message to the log using the {@link LogLevel#FATAL} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default Logger fatal(@NonNull String format, @NonNull Object... args) {
        return this.log(LogLevel.FATAL, String.format(format, args));
    }

    /**
     * Writes an exception stack trace to the log using the {@link LogLevel#FATAL} level.
     *
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger fatal(@NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        getStackTrace(throwable, joiner::add);
        return this.fatal(joiner.toString());
    }

    /**
     * Writes a plain message to the log using the {@link LogLevel#ALERT} level.
     *
     * @param message the message to be written
     */
    default Logger alert(@NonNull String message) {
        return this.log(LogLevel.ALERT, message);
    }

    /**
     * Writes a formatted message to the log using the {@link LogLevel#ALERT} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default Logger alert(@NonNull String format, @NonNull Object... args) {
        return this.log(LogLevel.ALERT, String.format(format, args));
    }

    /**
     * Writes an exception stack trace to the log using the {@link LogLevel#ALERT} level.
     *
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger alert(@NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        getStackTrace(throwable, joiner::add);
        return this.alert(joiner.toString());
    }

    /**
     * Writes a plain message to the log using the {@link LogLevel#WARN} level.
     *
     * @param message the message to be written
     */
    default Logger warn(@NonNull String message) {
        return this.log(LogLevel.WARN, message);
    }

    /**
     * Writes a formatted message to the log using the {@link LogLevel#WARN} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default Logger warn(@NonNull String format, @NonNull Object... args) {
        return this.log(LogLevel.WARN, String.format(format, args));
    }

    /**
     * Writes an exception stack trace to the log using the {@link LogLevel#WARN} level.
     *
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger warn(@NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        getStackTrace(throwable, joiner::add);
        return this.warn(joiner.toString());
    }

    /**
     * Writes a plain message to the log using the {@link LogLevel#TRACE} level.
     *
     * @param message the message to be written
     */
    default Logger trace(@NonNull String message) {
        return this.log(LogLevel.TRACE, message);
    }

    /**
     * Writes a formatted message to the log using the {@link LogLevel#TRACE} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default Logger trace(@NonNull String format, @NonNull Object... args) {
        return this.log(LogLevel.TRACE, String.format(format, args));
    }

    /**
     * Writes an exception stack trace to the log using the {@link LogLevel#TRACE} level.
     *
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger trace(@NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        getStackTrace(throwable, joiner::add);
        return this.trace(joiner.toString());
    }

    /**
     * Writes a plain message to the log using the {@link LogLevel#DEBUG} level.
     *
     * @param message the message to be written
     */
    default Logger debug(@NonNull String message) {
        return this.log(LogLevel.DEBUG, message);
    }

    /**
     * Writes a formatted message to the log using the {@link LogLevel#DEBUG} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format the message to be formatted
     * @param args   the arguments to be used for formatting
     */
    default Logger debug(@NonNull String format, @NonNull Object... args) {
        return this.log(LogLevel.DEBUG, String.format(format, args));
    }

    /**
     * Writes an exception stack trace to the log using the {@link LogLevel#DEBUG} level.
     *
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger debug(@NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        getStackTrace(throwable, joiner::add);
        return this.debug(joiner.toString());
    }

    //
    //
    // Other stuff
    //
    //

    /**
     * Gets the currently used {@link FormatParser} for parsing formatted log messages.
     *
     * @return the currently used {@link FormatParser}
     */
    FormatParser getFormatParser();

    /**
     * Sets the currently used {@link FormatParser} for parsing formatted log messages.
     *
     * @param parser the new {@link FormatParser} to use
     */
    Logger setFormatParser(@NonNull FormatParser parser);

    /**
     * Gets the currently used {@link MessageFormatter} for formatting log messages for printing.
     *
     * @return the currently used {@link MessageFormatter}
     */
    MessageFormatter getMessageFormatter();

    /**
     * Sets the currently used {@link MessageFormatter} for formatting log messages for printing.
     *
     * @param formatter the new {@link MessageFormatter} to use
     */
    Logger setMessageFormatter(@NonNull MessageFormatter formatter);

    /**
     * Gets the currently used {@link MessagePrinter} for printing log messages.
     *
     * @return the currently used {@link MessagePrinter}
     */
    MessagePrinter getMessagePrinter();

    /**
     * Sets the currently used {@link MessagePrinter} for printing log messages.
     *
     * @param printer the new {@link MessagePrinter} to use
     */
    Logger setMessagePrinter(@NonNull MessagePrinter printer);

    /**
     * Gets the currently used header above messages printed with the {@link LogLevel#ALERT} level.
     * <p>
     * Defaults to {@link #DEFAULT_ALERT_HEADER}.
     *
     * @return the current alert header
     */
    TextComponent getAlertHeader();

    /**
     * Sets the currently used header above messages printed with the {@link LogLevel#ALERT} level.
     * <p>
     * Defaults to {@link #DEFAULT_ALERT_HEADER}.
     *
     * @param alertHeader the new alert header to use
     */
    Logger setAlertHeader(@NonNull TextComponent alertHeader);

    /**
     * Gets the currently used prefix above messages printed with the {@link LogLevel#ALERT} level.
     * <p>
     * Defaults to {@link #DEFAULT_ALERT_PREFIX}.
     *
     * @return the current alert prefix
     */
    TextComponent getAlertPrefix();

    /**
     * Sets the currently used prefix above messages printed with the {@link LogLevel#ALERT} level.
     * <p>
     * Defaults to {@link #DEFAULT_ALERT_PREFIX}.
     *
     * @param alertPrefix the new alert prefix to use
     */
    Logger setAlertPrefix(@NonNull TextComponent alertPrefix);

    /**
     * Gets the currently used footer above messages printed with the {@link LogLevel#ALERT} level.
     * <p>
     * Defaults to {@link #DEFAULT_ALERT_FOOTER}.
     *
     * @return the current alert footer
     */
    TextComponent getAlertFooter();

    /**
     * Sets the currently used footer above messages printed with the {@link LogLevel#ALERT} level.
     * <p>
     * Defaults to {@link #DEFAULT_ALERT_FOOTER}.
     *
     * @param alertFooter the new alert footer to use
     */
    Logger setAlertFooter(@NonNull TextComponent alertFooter);

    /**
     * Gets the levels that may be printed by this logger.
     *
     * @return the levels that may be printed by this logger
     */
    Set<LogLevel> getLogLevels();

    /**
     * Sets the levels that may be printed by this logger.
     *
     * @param levels the new levels that may be printed by this logger
     */
    Logger setLogLevels(@NonNull Set<LogLevel> levels);

    /**
     * Sets the currently used log amount.
     *
     * @param amount the new log amount to use
     */
    default Logger setLogAmount(@NonNull LogAmount amount)    {
        return this.setLogLevels(amount.getLevelSet());
    }

    /**
     * Creates a new channel with the given name. The returned logger will print all it's output
     * via this one (in whatever manner the implementation chooses to do this), but will have an
     * additional prefix in the output.
     *
     * @param name the name of the channel
     * @return a channel with the given name
     */
    Logger channel(@NonNull String name);

    /**
     * Checks whether or not log messages with the given level should be displayed.
     *
     * @param level the level
     * @return whether or not messages with the given level will be displayed
     */
    default boolean shouldDisplay(@NonNull LogLevel level) {
        return this.getLogLevels().contains(level);
    }
}
