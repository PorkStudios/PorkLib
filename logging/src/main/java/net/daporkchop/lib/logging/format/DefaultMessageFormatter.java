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

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.logging.LogLevel;
import net.daporkchop.lib.logging.console.TextFormat;
import net.daporkchop.lib.logging.format.component.TextComponent;
import net.daporkchop.lib.logging.format.component.TextComponentHolder;
import net.daporkchop.lib.logging.format.component.TextComponentString;

import java.awt.Color;
import java.sql.Date;
import java.text.DateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Default implementation of {@link MessageFormatter}. Prints messages as follows:
 * <p>
 * [dd/MM/yyyy HH:mm:ss] [channel] [level] message...
 *
 * @author DaPorkchop_
 */
@Builder
public class DefaultMessageFormatter implements MessageFormatter {
    protected static final TextComponent TOKEN_START = new TextComponentString("[");
    protected static final TextComponent TOKEN_END = new TextComponentString("] ");

    protected static final TextComponent NEWLINE = new TextComponentString("\n");

    protected static final Map<LogLevel, TextComponent> LEVEL_COMPONENTS = new EnumMap<>(LogLevel.class);

    static {
        for (LogLevel level : LogLevel.values()) {
            LEVEL_COMPONENTS.put(level, new TextComponentString(level.name(), level.getColor(), null, level.getStyle()));
        }
    }

    @Builder.Default
    protected final boolean alertSpecialFormatting = true;
    @NonNull
    @Builder.Default
    protected final TextComponent alertHeaderFooter = new TextComponentString("****************************************", Color.RED, null, TextStyle.BOLD);
    @NonNull
    @Builder.Default
    protected final TextComponent alertPrefix = new TextComponentString("* ", Color.RED, null, TextStyle.BOLD);

    @Builder.Default
    protected final boolean includeDate = true;
    @NonNull
    @Builder.Default
    protected final DateFormat dateFormat = PorkUtil.DATE_FORMAT;
    @NonNull
    @Builder.Default
    protected final TextFormat dateStyle = new TextFormat().setTextColor(Color.CYAN);

    @Builder.Default
    protected final boolean includeChannel = true;
    @NonNull
    @Builder.Default
    protected final TextFormat channelStyle = new TextFormat();

    @Builder.Default
    protected final boolean includeLevel = true;

    @Override
    public Stream<TextComponent> format(@NonNull LogLevel level, String channelName, @NonNull Stream<TextComponent> message) {
        TextComponent sharedPrefix = new TextComponentHolder();
        if (this.includeDate) {
            sharedPrefix.pushChild(TOKEN_START);
            sharedPrefix.pushChild(new TextComponentString(this.dateFormat.format(Date.from(Instant.now())), this.dateStyle));
            sharedPrefix.pushChild(TOKEN_END);
        }
        if (this.includeChannel && channelName != null) {
            sharedPrefix.pushChild(TOKEN_START);
            sharedPrefix.pushChild(new TextComponentString(channelName, this.channelStyle));
            sharedPrefix.pushChild(TOKEN_END);
        }
        if (this.includeLevel) {
            sharedPrefix.pushChild(TOKEN_START);
            sharedPrefix.pushChild(LEVEL_COMPONENTS.get(level));
            sharedPrefix.pushChild(TOKEN_END);
        }

        if (this.alertSpecialFormatting && level == LogLevel.ALERT) {
            return Stream.of(
                    Stream.of(
                            new TextComponentHolder(Arrays.asList(sharedPrefix, this.alertHeaderFooter)),
                            new TextComponentHolder(Arrays.asList(sharedPrefix, this.alertPrefix))),
                    message.map(line -> new TextComponentHolder(Arrays.asList(sharedPrefix, this.alertPrefix, line))),
                    Stream.of(
                            new TextComponentHolder(Arrays.asList(sharedPrefix, this.alertPrefix)),
                            new TextComponentHolder(Arrays.asList(sharedPrefix, this.alertHeaderFooter))))
                    .flatMap(Function.identity());
        } else {
            return message.map(line -> new TextComponentHolder(Arrays.asList(sharedPrefix, line)));
        }
    }
}
