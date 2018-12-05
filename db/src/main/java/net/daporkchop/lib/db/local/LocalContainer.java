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

package net.daporkchop.lib.db.local;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.function.IOConsumer;
import net.daporkchop.lib.db.Container;
import net.daporkchop.lib.db.PorkDB;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author DaPorkchop_
 */
public interface LocalContainer<V, B extends Container.Builder<V, ? extends LocalContainer<V, B, DB>, DB>, DB extends LocalDB> extends Container<V, B, DB> {
    File getFile();
    
    default File getFile(@NonNull String name) throws IOException {
        return this.getFile(name, null, true);
    }

    default File getFile(@NonNull String name, boolean create) throws IOException {
        return this.getFile(name, null, create);
    }

    default File getFile(@NonNull String name, IOConsumer<DataOut> initializer, boolean create) throws IOException {
        if (!this.usesDirectory()) {
            throw new IllegalStateException();
        } else {
            File file = new File(this.getFile(), name);
            if (create && !file.exists()) {
                File parent = file.getParentFile();
                boolean parentExists = parent.exists();
                if (parentExists && !parent.isDirectory()) {
                    throw this.exception("Not a directory: ${0}", parent);
                } else if (!parentExists && !parent.mkdirs()) {
                    throw this.exception("Could not create directory: ${0}", parent);
                } else if (!file.createNewFile()) {
                    throw this.exception("Could not create file: ${0}", file);
                } else if (initializer != null) {
                    try (DataOut out = DataOut.wrap(file)) {
                        initializer.acceptThrowing(out);
                    }
                }
            }
            return file;
        }
    }

    default DataIn getIn(@NonNull String name) throws IOException {
        return this.getIn(name, null);
    }

    default DataIn getIn(@NonNull String name, IOConsumer<DataOut> initializer) throws IOException {
        return DataIn.wrap(this.getFile(name, initializer, true));
    }

    default DataOut getOut(@NonNull String name) throws IOException {
        return this.getOut(name, null);
    }

    default DataOut getOut(@NonNull String name, IOConsumer<DataOut> initializer) throws IOException {
        return DataOut.wrap(this.getFile(name, initializer, true));
    }

    default RandomAccessFile getRAF(@NonNull String name) throws IOException {
        return this.getRAF(name, null);
    }

    default RandomAccessFile getRAF(@NonNull String name, IOConsumer<DataOut> initializer) throws IOException {
        return new RandomAccessFile(this.getFile(name, initializer, true), "rw");
    }
}
