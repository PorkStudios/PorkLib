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
