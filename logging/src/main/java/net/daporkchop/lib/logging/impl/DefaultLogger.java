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

import lombok.NonNull;
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.common.misc.Tuple;
import net.daporkchop.lib.logging.LogAmount;
import net.daporkchop.lib.logging.LogLevel;
import net.daporkchop.lib.logging.Logger;
import net.daporkchop.lib.logging.console.ansi.ANSIMessagePrinter;
import net.daporkchop.lib.logging.format.MessagePrinter;
import net.daporkchop.lib.logging.format.SelfClosingMessagePrinter;
import net.daporkchop.lib.logging.format.component.TextComponent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The default, global logger instance, accessible via {@link net.daporkchop.lib.logging.Logging#logger}.
 * <p>
 * This logger supports forwarding log messages on to multiple printers as opposed to just one, allowing for log messages to e.g. be stored to a
 * file and printed to the console simultaneously.
 *
 * @author DaPorkchop_
 */
public class DefaultLogger extends SimpleLogger {
    public static final PrintStream stdOut = System.out;
    protected static final AtomicBoolean hasRedirectedStdOut = new AtomicBoolean(false);

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
    public void redirectStdOut() {
        if (!hasRedirectedStdOut.getAndSet(true)) {
            try {
                Logger fakeLogger = this.channel("stdout");
                System.setOut(new PrintStream(new OutputStream() {
                    public ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    @Override
                    public synchronized void write(int b) throws IOException {
                        if (b == '\n') {
                            fakeLogger.info(new String(this.baos.toByteArray(), UTF8.utf8));
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
                            fakeLogger.error(new String(this.baos.toByteArray(), UTF8.utf8));
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
    }

    /**
     * Enables ANSI text formatting on this logger.
     */
    public DefaultLogger enableANSI() {
        this.delegates.computeIfAbsent("console", s -> new Tuple<>(this.logLevels, null)).atomicSetB(new ANSIMessagePrinter());
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
        PrintStream printStream;
        try {
            printStream = overwrite ? new PrintStream(path) : new PrintStream(new FileOutputStream(path, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this.setDelegate(name, new SelfClosingMessagePrinter<PrintStream>(printStream) {
            @Override
            public void accept(@NonNull TextComponent component) {
                this.resource.println(component.toRawString());
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
    public SimpleLogger setMessagePrinter(@NonNull MessagePrinter messagePrinter) {
        this.warn("Attempted to call setMessagePrinter() on an instance of %s!", DefaultLogger.class);
        return this;
    }
}
