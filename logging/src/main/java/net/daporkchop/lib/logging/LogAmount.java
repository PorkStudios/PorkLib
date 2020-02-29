/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
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

import java.util.Collections;
import java.util.EnumMap;
import java.util.Set;

/**
 * Presets for which messages should be printed on a specific logger
 *
 * @author DaPorkchop_
 */
public enum LogAmount {
    /**
     * Prints nothing at all.
     */
    NONE(),
    /**
     * Only basic information useful for end users.
     * <p>
     * Prints messages sent on {@link LogLevel#INFO}, {@link LogLevel#SUCCESS}, {@link LogLevel#ERROR}, {@link LogLevel#FATAL} and
     * {@link LogLevel#ALERT} levels.
     */
    SIMPLE(LogLevel.INFO, LogLevel.SUCCESS, LogLevel.ERROR, LogLevel.FATAL, LogLevel.ALERT),
    /**
     * Prints everything from {@link #SIMPLE}, as well as {@link LogLevel#WARN}.
     */
    NORMAL(LogAmount.SIMPLE, LogLevel.WARN),
    /**
     * Prints everything from {@link #SIMPLE}, as well as {@link LogLevel#TRACE}.
     */
    TRACE(LogAmount.NORMAL, LogLevel.TRACE),
    /**
     * Prints everything, including messages sent with {@link LogLevel#DEBUG}.
     */
    DEBUG(LogLevel.values()),
    ;

    protected final Set<LogLevel> levels;

    LogAmount(@NonNull Enum... printable) {
        this.levels = LogLevel.emptySet();

        for (Enum value : printable) {
            if (value instanceof LogLevel) {
                this.levels.add((LogLevel) value);
            } else if (value instanceof LogAmount) {
                this.levels.addAll(((LogAmount) value).levels);
            } else {
                throw new IllegalArgumentException(String.format("Invalid type: %s", value == null ? "null" : value.getClass().getCanonicalName()));
            }
        }
    }

    public Set<LogLevel> getLevelSet()  {
        Set<LogLevel> levels = LogLevel.emptySet();
        levels.addAll(this.levels);
        return levels;
    }

    public boolean shouldPrint(@NonNull LogLevel level) {
        return this.levels.contains(level);
    }
}
