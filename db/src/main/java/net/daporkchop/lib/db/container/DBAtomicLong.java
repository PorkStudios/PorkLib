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

package net.daporkchop.lib.db.container;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.db.Container;
import net.daporkchop.lib.db.PorkDB;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An {@link AtomicLong} stored in a database
 *
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

    public static Builder builder(@NonNull PorkDB db, @NonNull String name) {
        return new Builder(db, name);
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
        private Builder(PorkDB db, String name) {
            super(db, name);
        }

        public String getTest() {
            return "";
        }

        @Override
        protected DBAtomicLong buildImpl() throws IOException {
            return new DBAtomicLong(this);
        }
    }
}
