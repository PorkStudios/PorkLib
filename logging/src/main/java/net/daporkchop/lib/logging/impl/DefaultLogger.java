package net.daporkchop.lib.logging.impl;

import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.logging.Logger;

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
    protected static final PrintStream stdOut = System.out;
    protected static AtomicBoolean hasRedirectedStdOut = new AtomicBoolean(false);

    public DefaultLogger()  {
        super(stdOut::println);
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
}
