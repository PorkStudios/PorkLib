/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2021 DaPorkchop_
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

package net.daporkchop.lib.logging.format;

import lombok.NonNull;
import net.daporkchop.lib.logging.LogLevel;
import net.daporkchop.lib.logging.format.component.TextComponent;

import java.util.stream.Stream;

/**
 * Formats log messages for printing to the console
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface MessageFormatter {
    /**
     * Prepares the actual message for printing
     *
     * @param level       the log level that the message was sent using
     * @param channelName the name of the channel that the message was sent on. May be {@code null}
     * @param message     the actual message
     * @return a {@link Stream} containing each line of the formatted message, ready to be printed to the console
     */
    Stream<TextComponent> format(@NonNull LogLevel level, String channelName, @NonNull Stream<TextComponent> message);
}
