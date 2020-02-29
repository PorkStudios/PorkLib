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

package net.daporkchop.lib.logging.impl;

import lombok.AllArgsConstructor;
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
import net.daporkchop.lib.logging.format.MessagePrinter;
import net.daporkchop.lib.logging.format.component.TextComponent;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class SimpleLogger implements Logger {
    @NonNull
    protected FormatParser formatParser = new DefaultFormatParser();
    @NonNull
    protected MessageFormatter messageFormatter = new DefaultMessageFormatter();
    @NonNull
    protected MessagePrinter messagePrinter;
    @NonNull
    protected TextComponent alertHeader = DEFAULT_ALERT_HEADER;
    @NonNull
    protected TextComponent alertPrefix = DEFAULT_ALERT_PREFIX;
    @NonNull
    protected TextComponent alertFooter = DEFAULT_ALERT_FOOTER;

    @NonNull
    protected Set<LogLevel> logLevels = LogAmount.NORMAL.getLevelSet();

    protected final Map<String, ChannelLogger> channelCache = Collections.synchronizedMap(PorkUtil.newSoftCache());

    public SimpleLogger(@NonNull MessagePrinter messagePrinter, @NonNull Set<LogLevel> logLevels)    {
        this.messagePrinter = messagePrinter;
        this.logLevels = logLevels;
    }

    public SimpleLogger(@NonNull MessagePrinter messagePrinter, @NonNull LogAmount amount)    {
        this(messagePrinter, amount.getLevelSet());
    }

    @Override
    public Logger log(@NonNull LogLevel level, @NonNull String message) {
        this.doLog(level, null, message);
        return this;
    }

    protected synchronized void doLog(@NonNull LogLevel level, String channel, @NonNull String message) {
        this.doLog(level, channel, this.formatParser.parse(message));
    }

    protected synchronized void doLog(@NonNull LogLevel level, String channel, @NonNull TextComponent message)  {
        if (message.hasNewline())   {
            this.doLog(level, channel, message.splitOnNewlines());
        } else {
            this.doLog(level, channel, Collections.singletonList(message));
        }
    }

    protected synchronized void doLog(@NonNull LogLevel level, String channel, @NonNull List<TextComponent> lines)  {
        Date date = Date.from(Instant.now());

        if (level == LogLevel.ALERT) {
            TextComponent prefixFormatted = this.messageFormatter.format(date, channel, level, this.alertPrefix);

            this.doLog(level, this.messageFormatter.format(date, channel, level, this.alertHeader));
            this.doLog(level, prefixFormatted);
            for (TextComponent line : lines) {
                this.doLog(level, prefixFormatted);
                this.doLog(level, line);
            }
            this.doLog(level, prefixFormatted);
            this.doLog(level, this.messageFormatter.format(date, channel, level, this.alertFooter));
        } else {
            for (TextComponent line : lines) {
                this.doLog(level, this.messageFormatter.format(date, channel, level, line));
            }
        }
    }

    protected synchronized void doLog(@NonNull LogLevel level, @NonNull TextComponent component) {
        this.messagePrinter.accept(component);
    }

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
        public MessagePrinter getMessagePrinter() {
            return SimpleLogger.this.getMessagePrinter();
        }

        @Override
        public Logger setMessagePrinter(@NonNull MessagePrinter printer) {
            return SimpleLogger.this.setMessagePrinter(printer);
        }

        @Override
        public TextComponent getAlertHeader() {
            return SimpleLogger.this.getAlertHeader();
        }

        @Override
        public Logger setAlertHeader(@NonNull TextComponent alertHeader) {
            return SimpleLogger.this.setAlertHeader(alertHeader);
        }

        @Override
        public TextComponent getAlertPrefix() {
            return SimpleLogger.this.getAlertPrefix();
        }

        @Override
        public Logger setAlertPrefix(@NonNull TextComponent alertPrefix) {
            return SimpleLogger.this.setAlertPrefix(alertPrefix);
        }

        @Override
        public TextComponent getAlertFooter() {
            return SimpleLogger.this.getAlertFooter();
        }

        @Override
        public Logger setAlertFooter(@NonNull TextComponent alertFooter) {
            return SimpleLogger.this.setAlertFooter(alertFooter);
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
            return SimpleLogger.this.channel(name);
        }
    }
}
