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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.logging.format.TextStyle;

import java.awt.Color;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Set;

/**
 * Log messages are sent on specific log level to indicate their importance. Loggers may be configured to only print messages on specific log levels.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public enum LogLevel {
    INFO(null, 0),
    SUCCESS(Color.GREEN, TextStyle.BOLD),
    ERROR(Color.RED, TextStyle.BOLD),
    FATAL(Color.RED, TextStyle.BOLD | TextStyle.BLINKING),
    ALERT(Color.RED, TextStyle.BOLD | TextStyle.BLINKING),
    WARN(Color.ORANGE, TextStyle.ITALIC),
    TRACE(null, TextStyle.ITALIC),
    DEBUG(Color.GRAY, TextStyle.ITALIC),
    ;

    public static Set<LogLevel> emptySet()  {
        return Collections.newSetFromMap(new EnumMap<>(LogLevel.class));
    }

    public static Set<LogLevel> set(LogLevel... levels) {
        Set<LogLevel> set = emptySet();
        if (levels != null)   {
            Collections.addAll(set, levels);
        }
        return set;
    }

    protected final Color color;
    protected final int style;
}
