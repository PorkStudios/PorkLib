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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.db.container.AbstractContainer;
import net.daporkchop.lib.db.PorkDB;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * An implementation of {@link PorkDB} that runs on the local machine
 *
 * @author DaPorkchop_
 */
@Getter
public class LocalDB extends PorkDB<LocalDB, LocalContainer<?, ?, LocalDB>> {
    private final File root;
    private final Object saveLock = new Object();

    protected LocalDB(@NonNull Builder builder) {
        super(builder);

        this.root = builder.root;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public void close() throws IOException {
        this.ensureOpen();
        synchronized (this.saveLock) { //TODO: read-write locking implementation
            this.open = false;
            try {
                for (Iterator<LocalContainer<?, ?, LocalDB>> iter = this.loadedContainers.values().iterator(); iter.hasNext(); iter.next().close()) {
                }
            } finally {
                this.loadedContainers.clear();
            }
        }
    }

    @Override
    public void save() throws IOException {
        this.ensureOpen();
        synchronized (this.saveLock) {
            for (LocalContainer<?, ?, LocalDB> container : this.loadedContainers.values()) {
                container.save();
            }
        }
    }

    @RequiredArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Getter
    @Setter
    public static class Builder extends PorkDB.Builder<Builder, LocalDB>  {
        /**
         * The root directory of the database.
         */
        @NonNull
        private File root;

        @Override
        public LocalDB build() {
            if (this.root == null)  {
                throw new IllegalStateException("root must be set!");
            }

            return new LocalDB(this);
        }
    }
}
