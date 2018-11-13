/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.db.container.bitset;

import com.zaxxer.sparsebits.SparseBitSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.daporkchop.lib.binary.Persistent;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author DaPorkchop_
 */
@Getter
@RequiredArgsConstructor
public class PersistentSparseBitSet implements Persistent {
    @Setter
    private volatile boolean dirty;
    private final File file;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private SparseBitSet bitSet;

    @Override
    public void load() throws IOException {
        if (this.file.exists()) {
            if (!this.file.isFile())    {
                throw new IllegalStateException(String.format("Not a file: %s", this.file.getAbsolutePath()));
            }
            if (this.file.length() != 0L) {
                try (ObjectInput in = new ObjectInputStream(DataIn.wrap(this.file))) {
                    this.bitSet = (SparseBitSet) in.readObject();
                    return;
                } catch (ClassNotFoundException e)  {
                    throw new RuntimeException(e);
                }
            }
        } else if (!this.file.createNewFile())  {
            throw new IllegalStateException(String.format("Could not create file: %s", this.file.getAbsolutePath()));
        }
        this.bitSet = new SparseBitSet();
    }

    @Override
    public void save() throws IOException {
        this.lock.writeLock().lock();
        try {
            if (this.dirty) {
                this.dirty = false;
                try (ObjectOutput out = new ObjectOutputStream(DataOut.wrap(this.file))) {
                    out.writeObject(this.bitSet);
                }
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public boolean get(int pos) {
        return this.bitSet.get(pos);
    }

    public void set(int pos, boolean state) {
        this.bitSet.set(pos, state);
    }

    public void set(int pos) {
        this.set(pos, true);
    }

    public void clear(int pos) {
        this.set(pos, false);
    }

    public void clear() {
        this.bitSet.clear();
    }
}
