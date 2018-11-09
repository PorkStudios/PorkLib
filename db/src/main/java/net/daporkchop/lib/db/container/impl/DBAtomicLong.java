package net.daporkchop.lib.db.container.impl;

import lombok.Getter;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.db.container.Container;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@Getter
public class DBAtomicLong extends Container<AtomicLong, DBAtomicLong.Builder> {
    private final AtomicLong value = new AtomicLong();

    public DBAtomicLong(Builder builder) throws IOException {
        super(builder);

        try (DataIn in = DataIn.wrap(new FileInputStream(this.file))) {
            this.value.set(in.readLong());
        }
    }

    public long get() {
        return this.value.get();
    }

    public void set(long l) {
        this.value.set(l);
    }

    public long getAndSet(long l) {
        return this.value.getAndSet(l);
    }

    public long addAndGet(long l) {
        return this.value.addAndGet(l);
    }

    public long getAndAdd(long l) {
        return this.value.getAndAdd(l);
    }

    public long getAndIncrement() {
        return this.value.getAndIncrement();
    }

    public long incrementAndGet() {
        return this.value.incrementAndGet();
    }

    public long getAndDecrement() {
        return this.value.getAndDecrement();
    }

    public long decrementAndGet() {
        return this.value.decrementAndGet();
    }

    @Override
    protected boolean usesDirectory() {
        return false;
    }

    @Override
    public void save() throws IOException {
        try (DataOut out = DataOut.wrap(new FileOutputStream(this.file))) {
            out.writeLong(this.value.get());
        }
    }

    public static final class Builder extends Container.Builder<AtomicLong, DBAtomicLong> {
        public Builder(PorkDB db, String name, Consumer<DBAtomicLong> buildHook) {
            super(db, name, buildHook);
        }

        @Override
        protected DBAtomicLong buildImpl() throws IOException {
            return new DBAtomicLong(this);
        }
    }
}
