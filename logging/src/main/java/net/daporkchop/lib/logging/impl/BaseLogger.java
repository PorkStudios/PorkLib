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

package net.daporkchop.lib.logging.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.daporkchop.lib.logging.LogAmount;
import net.daporkchop.lib.logging.LogLevel;
import net.daporkchop.lib.logging.Logger;
import net.daporkchop.lib.logging.format.DefaultFormatParser;
import net.daporkchop.lib.logging.format.DefaultMessageFormatter;
import net.daporkchop.lib.logging.format.FormatParser;
import net.daporkchop.lib.logging.format.MessageFormatter;
import net.daporkchop.lib.logging.format.MessagePrinter;
import net.daporkchop.lib.logging.format.component.TextComponent;
import net.daporkchop.lib.logging.format.component.TextComponentHolder;

import java.util.Date;
import java.time.Instant;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class BaseLogger implements Logger {
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
    protected LogAmount logAmount = LogAmount.NORMAL;

    @Override
    public void log(@NonNull LogLevel level, @NonNull String message) {
        this.doLog(level, null, message);
    }

    protected synchronized void doLog(@NonNull LogLevel level, String channel, @NonNull String message) {
        if (!this.shouldDisplay(level)) {
            return;
        }
        Date date = Date.from(Instant.now());
        TextComponent component = this.formatParser.parse(message);
        if (level == LogLevel.ALERT) {
            this.messagePrinter.accept(this.messageFormatter.format(date, channel, level, this.alertHeader));
            this.messagePrinter.accept(this.messageFormatter.format(date, channel, level, this.alertPrefix));
            if (component.hasNewline()) {
                for (TextComponent line : component.splitOnNewlines())  {
                    this.messagePrinter.accept(this.messageFormatter.format(date, channel, level, this.alertFooter.insertToHeadOf(line)));
                }
            } else {
                this.messagePrinter.accept(this.messageFormatter.format(date, channel, level, this.alertFooter.insertToHeadOf(component)));
            }
            this.messagePrinter.accept(this.messageFormatter.format(date, channel, level, this.alertPrefix));
            this.messagePrinter.accept(this.messageFormatter.format(date, channel, level, this.alertFooter));
        } else {
            if (component.hasNewline()) {
                for (TextComponent line : component.splitOnNewlines())  {
                    this.messagePrinter.accept(this.messageFormatter.format(date, channel, level, line));
                }
            } else {
                this.messagePrinter.accept(this.messageFormatter.format(date, channel, level, component));
            }
        }
    }

    @Override
    public Logger channel(@NonNull String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Channel name may not be empty!");
        } else {
            return new Logger() { //TODO: copy the settings from parent logger rather than sharing them
                @Override
                public void log(@NonNull LogLevel level, @NonNull String message) {
                    BaseLogger.this.doLog(level, name, message);
                }

                @Override
                public FormatParser getFormatParser() {
                    return BaseLogger.this.getFormatParser();
                }

                @Override
                public void setFormatParser(@NonNull FormatParser parser) {
                    BaseLogger.this.setFormatParser(parser);
                }

                @Override
                public MessageFormatter getMessageFormatter() {
                    return BaseLogger.this.getMessageFormatter();
                }

                @Override
                public void setMessageFormatter(@NonNull MessageFormatter formatter) {
                    BaseLogger.this.setMessageFormatter(formatter);
                }

                @Override
                public MessagePrinter getMessagePrinter() {
                    return BaseLogger.this.getMessagePrinter();
                }

                @Override
                public void setMessagePrinter(@NonNull MessagePrinter printer) {
                    BaseLogger.this.setMessagePrinter(printer);
                }

                @Override
                public TextComponent getAlertHeader() {
                    return BaseLogger.this.getAlertHeader();
                }

                @Override
                public void setAlertHeader(@NonNull TextComponent alertHeader) {
                    BaseLogger.this.setAlertHeader(alertHeader);
                }

                @Override
                public TextComponent getAlertPrefix() {
                    return BaseLogger.this.getAlertPrefix();
                }

                @Override
                public void setAlertPrefix(@NonNull TextComponent alertPrefix) {
                    BaseLogger.this.setAlertPrefix(alertPrefix);
                }

                @Override
                public TextComponent getAlertFooter() {
                    return BaseLogger.this.getAlertFooter();
                }

                @Override
                public void setAlertFooter(@NonNull TextComponent alertFooter) {
                    BaseLogger.this.setAlertFooter(alertFooter);
                }

                @Override
                public LogAmount getLogAmount() {
                    return BaseLogger.this.getLogAmount();
                }

                @Override
                public void setLogAmount(@NonNull LogAmount amount) {
                    BaseLogger.this.setLogAmount(amount);
                }

                @Override
                public Logger channel(@NonNull String name) {
                    return BaseLogger.this.channel(name);
                }
            };
        }
    }
}
