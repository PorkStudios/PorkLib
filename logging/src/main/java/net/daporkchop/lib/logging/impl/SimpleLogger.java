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

package net.daporkchop.lib.logging.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.logging.LogAmount;
import net.daporkchop.lib.logging.LogLevel;
import net.daporkchop.lib.logging.Logger;
import net.daporkchop.lib.logging.format.DefaultFormatParser;
import net.daporkchop.lib.logging.format.DefaultMessageFormatter;
import net.daporkchop.lib.logging.format.FormatParser;
import net.daporkchop.lib.logging.format.MessageFormatter;
import net.daporkchop.lib.logging.format.component.TextComponent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public abstract class SimpleLogger implements Logger {
    @NonNull
    protected FormatParser formatParser;
    @NonNull
    protected MessageFormatter messageFormatter;

    @NonNull
    protected Set<LogLevel> logLevels = LogAmount.NORMAL.getLevelSet();

    protected final Map<String, ChannelLogger> channelCache = Collections.synchronizedMap(new HashMap<>()); //TODO: i want this to have weak values

    public SimpleLogger() {
        this(new DefaultFormatParser(), DefaultMessageFormatter.builder().build());
    }

    public SimpleLogger(@NonNull Set<LogLevel> logLevels) {
        this();
        this.logLevels = logLevels;
    }

    @Override
    public Logger log(@NonNull LogLevel level, @NonNull String message) {
        this.doLog(level, null, message);
        return this;
    }

    protected void doLog(@NonNull LogLevel level, String channel, @NonNull String message) {
        this.doLog(level, this.messageFormatter.format(level, channel, this.formatParser.parse(message).splitOnNewlines()));
    }

    protected abstract void doLog(@NonNull LogLevel level, @NonNull Stream<TextComponent> messageLines);

    @Override
    public Logger channel(@NonNull String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Channel name may not be empty!");
        } else {
            return this.channelCache.computeIfAbsent(name, ChannelLogger::new);
        }
    }

    @RequiredArgsConstructor
    @Getter
    protected class ChannelLogger implements Logger { //TODO: copy the settings from parent logger rather than sharing them
        @NonNull
        protected final String name;

        @Override
        public Logger log(@NonNull LogLevel level, @NonNull String message) {
            SimpleLogger.this.doLog(level, this.name, message);
            return this;
        }

        @Override
        public FormatParser getFormatParser() {
            return SimpleLogger.this.getFormatParser();
        }

        @Override
        public Logger setFormatParser(@NonNull FormatParser parser) {
            return SimpleLogger.this.setFormatParser(parser);
        }

        @Override
        public MessageFormatter getMessageFormatter() {
            return SimpleLogger.this.getMessageFormatter();
        }

        @Override
        public Logger setMessageFormatter(@NonNull MessageFormatter formatter) {
            return SimpleLogger.this.setMessageFormatter(formatter);
        }

        @Override
        public Set<LogLevel> getLogLevels() {
            return SimpleLogger.this.getLogLevels();
        }

        @Override
        public Logger setLogLevels(@NonNull Set<LogLevel> levels) {
            return SimpleLogger.this.setLogLevels(levels);
        }

        @Override
        public Logger channel(@NonNull String name) {
            return SimpleLogger.this.channel(this.name + "/" + name);
        }
    }
}
