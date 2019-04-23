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

/**
 * Utility class to help with writing log messages
 *
 * @author DaPorkchop_
 */
public interface Logger {
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
}
