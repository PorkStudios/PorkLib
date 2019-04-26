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
