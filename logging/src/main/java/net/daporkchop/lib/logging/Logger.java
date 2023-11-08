/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.logging;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.misc.SlashDevSlashNull;
import net.daporkchop.lib.logging.format.FormatParser;
import net.daporkchop.lib.logging.format.MessageFormatter;

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
        throwable.printStackTrace(new PrintWriter(SlashDevSlashNull.getOutputStream(), true) {
            @Override
            public void println(Object x) {
                linePrinter.accept(String.valueOf(x).replace("\t", "    "));
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
     * Writes an exception stack trace with a plain message to the log using the {@link LogLevel#INFO} level.
     *
     * @param message an additional message that will be displayed
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger info(@NonNull String message, @NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(message).add("");
        getStackTrace(throwable, joiner::add);
        return this.info(joiner.toString());
    }

    /**
     * Writes an exception stack trace with a formatted message to the log using the {@link LogLevel#INFO} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format an additional message that will be displayed
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger info(@NonNull String format, @NonNull Throwable throwable, @NonNull Object... args) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(String.format(format, args)).add("");
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
     * Writes an exception stack trace with a plain message to the log using the {@link LogLevel#SUCCESS} level.
     *
     * @param message an additional message that will be displayed
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger success(@NonNull String message, @NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(message).add("");
        getStackTrace(throwable, joiner::add);
        return this.success(joiner.toString());
    }

    /**
     * Writes an exception stack trace with a formatted message to the log using the {@link LogLevel#SUCCESS} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format an additional message that will be displayed
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger success(@NonNull String format, @NonNull Throwable throwable, @NonNull Object... args) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(String.format(format, args)).add("");
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
     * Writes an exception stack trace with a plain message to the log using the {@link LogLevel#ERROR} level.
     *
     * @param message an additional message that will be displayed
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger error(@NonNull String message, @NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(message).add("");
        getStackTrace(throwable, joiner::add);
        return this.error(joiner.toString());
    }

    /**
     * Writes an exception stack trace with a formatted message to the log using the {@link LogLevel#ERROR} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format an additional message that will be displayed
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger error(@NonNull String format, @NonNull Throwable throwable, @NonNull Object... args) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(String.format(format, args)).add("");
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
     * Writes an exception stack trace with a plain message to the log using the {@link LogLevel#FATAL} level.
     *
     * @param message an additional message that will be displayed
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger fatal(@NonNull String message, @NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(message).add("");
        getStackTrace(throwable, joiner::add);
        return this.fatal(joiner.toString());
    }

    /**
     * Writes an exception stack trace with a formatted message to the log using the {@link LogLevel#FATAL} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format an additional message that will be displayed
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger fatal(@NonNull String format, @NonNull Throwable throwable, @NonNull Object... args) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(String.format(format, args)).add("");
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
     * Writes an exception stack trace with a plain message to the log using the {@link LogLevel#ALERT} level.
     *
     * @param message an additional message that will be displayed
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger alert(@NonNull String message, @NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(message).add("");
        getStackTrace(throwable, joiner::add);
        return this.alert(joiner.toString());
    }

    /**
     * Writes an exception stack trace with a formatted message to the log using the {@link LogLevel#ALERT} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format an additional message that will be displayed
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger alert(@NonNull String format, @NonNull Throwable throwable, @NonNull Object... args) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(String.format(format, args)).add("");
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
     * Writes an exception stack trace with a plain message to the log using the {@link LogLevel#WARN} level.
     *
     * @param message an additional message that will be displayed
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger warn(@NonNull String message, @NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(message).add("");
        getStackTrace(throwable, joiner::add);
        return this.warn(joiner.toString());
    }

    /**
     * Writes an exception stack trace with a formatted message to the log using the {@link LogLevel#WARN} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format an additional message that will be displayed
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger warn(@NonNull String format, @NonNull Throwable throwable, @NonNull Object... args) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(String.format(format, args)).add("");
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
     * Writes an exception stack trace with a plain message to the log using the {@link LogLevel#TRACE} level.
     *
     * @param message an additional message that will be displayed
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger trace(@NonNull String message, @NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(message).add("");
        getStackTrace(throwable, joiner::add);
        return this.trace(joiner.toString());
    }

    /**
     * Writes an exception stack trace with a formatted message to the log using the {@link LogLevel#TRACE} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format an additional message that will be displayed
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger trace(@NonNull String format, @NonNull Throwable throwable, @NonNull Object... args) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(String.format(format, args)).add("");
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

    /**
     * Writes an exception stack trace with a plain message to the log using the {@link LogLevel#DEBUG} level.
     *
     * @param message an additional message that will be displayed
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger debug(@NonNull String message, @NonNull Throwable throwable) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(message).add("");
        getStackTrace(throwable, joiner::add);
        return this.debug(joiner.toString());
    }

    /**
     * Writes an exception stack trace with a formatted message to the log using the {@link LogLevel#DEBUG} level.
     * <p>
     * The message will be formatted using {@link String#format(String, Object...)}.
     *
     * @param format an additional message that will be displayed
     * @param throwable the exception whose stack trace should be printed
     */
    default Logger debug(@NonNull String format, @NonNull Throwable throwable, @NonNull Object... args) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(String.format(format, args)).add("");
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
