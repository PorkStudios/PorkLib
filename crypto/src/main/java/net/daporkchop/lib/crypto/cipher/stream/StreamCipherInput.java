package net.daporkchop.lib.crypto.cipher.stream;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bouncycastle.crypto.StreamCipher;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
public class StreamCipherInput extends InputStream {
    @NonNull
    private final Lock readLock;

    @NonNull
    private final StreamCipher cipher;

    @NonNull
    private final InputStream input;

    @Override
    public int read() throws IOException {
        this.readLock.lock();
        return this.cipher.returnByte((byte) this.input.read()) & 0xFF;
    }

    @Override
    public long skip(long n) throws IOException {
        this.readLock.lock();
        return this.input.skip(n);
    }

    @Override
    public int available() throws IOException {
        this.readLock.lock();
        return this.input.available();
    }

    @Override
    public void close() throws IOException {
        this.input.close();
        this.readLock.unlock();
    }
}
