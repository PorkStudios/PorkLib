package net.daporkchop.lib.crypto.cipher.stream;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bouncycastle.crypto.StreamCipher;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
public class StreamCipherOutput extends OutputStream {
    @NonNull
    private final Lock writeLock;

    @NonNull
    private final StreamCipher cipher;

    @NonNull
    private final OutputStream stream;

    @Override
    public void write(int b) throws IOException {
        this.stream.write(this.cipher.returnByte((byte) b) & 0xFF);
    }

    @Override
    public void flush() throws IOException {
        this.stream.flush();
    }

    @Override
    public void close() throws IOException {
        this.flush();
        this.writeLock.unlock();
    }
}
