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

import lombok.NonNull;
import net.daporkchop.lib.binary.oio.writer.UTF8FileWriter;
import net.daporkchop.lib.common.misc.Tuple;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.system.OperatingSystem;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.logging.LogAmount;
import net.daporkchop.lib.logging.LogLevel;
import net.daporkchop.lib.logging.Logger;
import net.daporkchop.lib.logging.console.ansi.ANSIMessagePrinter;
import net.daporkchop.lib.logging.format.FormatParser;
import net.daporkchop.lib.logging.format.MessageFormatter;
import net.daporkchop.lib.logging.format.MessagePrinter;
import net.daporkchop.lib.logging.format.SelfClosingMessagePrinter;
import net.daporkchop.lib.logging.format.component.TextComponent;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The default, global logger instance, accessible via {@link net.daporkchop.lib.logging.Logging#logger}.
 * <p>
 * This logger supports forwarding log messages on to multiple printers as opposed to just one, allowing for log messages to e.g. be stored to a
 * file and printed to the console simultaneously.
 *
 * @author DaPorkchop_
 */
public class DefaultLogger extends SimpleLogger {
    protected static final long REDIRECTEDSTDOUT_OFFSET = PUnsafe.pork_getOffset(DefaultLogger.class, "redirectedStdOut");

    public static final PrintStream stdOut = System.out;
    protected volatile int redirectedStdOut = 0;

    protected final Map<String, Tuple<Set<LogLevel>, MessagePrinter>> delegates = new ConcurrentHashMap<>();

    public DefaultLogger() {
        super(component -> {
            throw new UnsupportedOperationException("DefaultLogger's base message printer doesn't do anything!");
        });

        this.delegates.put("console", new Tuple<>(this.logLevels, component -> stdOut.println(component.toRawString())));
    }

    /**
     * Redirects {@link System#out} and {@link System#err} to this logger.
     * <p>
     * Be aware that this method may only be invoked once, and cannot be undone.
     */
    public DefaultLogger redirectStdOut() {
        if (PUnsafe.compareAndSwapInt(this, REDIRECTEDSTDOUT_OFFSET, 0, 1)) {
            try {
                Logger fakeLogger = this.channel("stdout");
                //TODO: optimize
                System.setOut(new PrintStream(new OutputStream() {
                    public ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    @Override
                    public synchronized void write(int b) throws IOException {
                        if (b == '\n') {
                            fakeLogger.info(new String(this.baos.toByteArray(), StandardCharsets.UTF_8));
                            this.baos.reset();
                        } else {
                            this.baos.write(b);
                        }
                    }
                }, true, "UTF-8"));
                System.setErr(new PrintStream(new OutputStream() {
                    public ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    @Override
                    public synchronized void write(int b) throws IOException {
                        if (b == '\n') {
                            fakeLogger.error(new String(this.baos.toByteArray(), StandardCharsets.UTF_8));
                            this.baos.reset();
                        } else {
                            this.baos.write(b);
                        }
                    }
                }, true, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    /**
     * Enables ANSI text formatting on this logger.
     */
    public DefaultLogger enableANSI() {
        if (PlatformInfo.OPERATING_SYSTEM == OperatingSystem.Windows)   {
            this.warn("Windows detected, not enabling ANSI formatting!");
        } else {
            this.delegates.computeIfAbsent("console", s -> new Tuple<>(this.logLevels, null)).atomicSetB(new ANSIMessagePrinter());
        }
        return this;
    }

    public DefaultLogger removeDelegate(@NonNull String name) {
        return this.setDelegate(name, null, null);
    }

    public DefaultLogger setDelegate(@NonNull String name, MessagePrinter printer)  {
        return this.setDelegate(name, null, printer);
    }

    public DefaultLogger setDelegate(@NonNull String name, Set<LogLevel> levels, MessagePrinter printer) {
        if (printer == null) {
            this.delegates.remove(name);
        } else {
            this.delegates.computeIfAbsent(name, s -> new Tuple<>(levels == null ? this.logLevels : levels, printer))
                          .setA(levels == null ? this.logLevels : levels).setB(printer);
        }
        return this;
    }

    // file methods
    public DefaultLogger addFile(@NonNull File path)    {
        return this.addFile(String.format("File:%s", path.getAbsolutePath()), path, true, null);
    }

    public DefaultLogger addFile(@NonNull File path, Set<LogLevel> levels)    {
        return this.addFile(String.format("File:%s", path.getAbsolutePath()), path, true, levels);
    }

    public DefaultLogger addFile(@NonNull File path, LogLevel... levels)    {
        return this.addFile(String.format("File:%s", path.getAbsolutePath()), path, true, LogLevel.set(levels));
    }

    public DefaultLogger addFile(@NonNull File path, @NonNull LogAmount amount)    {
        return this.addFile(String.format("File:%s", path.getAbsolutePath()), path, true, amount.getLevelSet());
    }

    public DefaultLogger addFile(@NonNull File path, boolean overwrite)    {
        return this.addFile(String.format("File:%s", path.getAbsolutePath()), path, overwrite, null);
    }

    public DefaultLogger addFile(@NonNull File path, boolean overwrite, Set<LogLevel> levels)    {
        return this.addFile(String.format("File:%s", path.getAbsolutePath()), path, overwrite, levels);
    }

    public DefaultLogger addFile(@NonNull File path, boolean overwrite, LogLevel... levels)    {
        return this.addFile(String.format("File:%s", path.getAbsolutePath()), path, overwrite, LogLevel.set(levels));
    }

    public DefaultLogger addFile(@NonNull File path, boolean overwrite, @NonNull LogAmount amount)    {
        return this.addFile(String.format("File:%s", path.getAbsolutePath()), path, overwrite, amount.getLevelSet());
    }

    public DefaultLogger addFile(@NonNull String name, @NonNull File path, boolean overwrite, Set<LogLevel> levels)    {
        PFiles.ensureFileExists(path);
        return this.setDelegate(name, levels, new SelfClosingMessagePrinter<UTF8FileWriter>(new UTF8FileWriter(path, !overwrite, PlatformInfo.OPERATING_SYSTEM.lineEnding(), true)) {
            @Override
            public void accept(@NonNull TextComponent component) {
                try {
                    this.resource.appendLn(component.toRawString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    protected synchronized void doLog(@NonNull LogLevel level, @NonNull TextComponent component) {
        for (Tuple<Set<LogLevel>, MessagePrinter> tuple : this.delegates.values()) {
            if (tuple.getA().contains(level))   {
                tuple.getB().accept(component);
            }
        }
    }

    @Override
    public synchronized DefaultLogger setLogLevels(@NonNull Set<LogLevel> logLevels) {
        Set<LogLevel> oldLevels = this.logLevels;
        super.setLogLevels(logLevels);
        for (Tuple<Set<LogLevel>, MessagePrinter> tuple : this.delegates.values())  {
            if (tuple.getA() == oldLevels)  {
                tuple.atomicSetA(logLevels);
            }
        }
        return this;
    }

    @Override
    public DefaultLogger setMessagePrinter(@NonNull MessagePrinter messagePrinter) {
        this.warn("Attempted to call setMessagePrinter() on an instance of %s!", DefaultLogger.class);
        return this;
    }

    @Override
    public DefaultLogger setFormatParser(@NonNull FormatParser formatParser) {
        super.setFormatParser(formatParser);
        return this;
    }

    @Override
    public DefaultLogger setMessageFormatter(@NonNull MessageFormatter messageFormatter) {
        super.setMessageFormatter(messageFormatter);
        return this;
    }

    @Override
    public DefaultLogger setAlertHeader(@NonNull TextComponent alertHeader) {
        super.setAlertHeader(alertHeader);
        return this;
    }

    @Override
    public DefaultLogger setAlertPrefix(@NonNull TextComponent alertPrefix) {
        super.setAlertPrefix(alertPrefix);
        return this;
    }

    @Override
    public DefaultLogger setAlertFooter(@NonNull TextComponent alertFooter) {
        super.setAlertFooter(alertFooter);
        return this;
    }

    @Override
    public DefaultLogger setLogAmount(@NonNull LogAmount amount) {
        super.setLogAmount(amount);
        return this;
    }
}
