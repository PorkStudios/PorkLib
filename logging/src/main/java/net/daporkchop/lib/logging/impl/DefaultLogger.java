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

import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.logging.Logger;
import net.daporkchop.lib.logging.console.ansi.ANSIMessagePrinter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author DaPorkchop_
 */
public class DefaultLogger extends BaseLogger {
    public static final PrintStream stdOut = System.out;
    protected static final AtomicBoolean hasRedirectedStdOut = new AtomicBoolean(false);

    public DefaultLogger()  {
        super(component -> stdOut.println(component.toRawString()));
    }

    public void redirectStdOut()    {
        if (!hasRedirectedStdOut.getAndSet(true))   {
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
            } catch (UnsupportedEncodingException   e)  {
                throw new RuntimeException(e);
            }
        }
    }

    public void enableANSI()    {
        this.setMessagePrinter(new ANSIMessagePrinter());
    }
}
