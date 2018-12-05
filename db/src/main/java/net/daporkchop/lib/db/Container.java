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

package net.daporkchop.lib.db;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.logging.Logging;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
public interface Container<V, B extends Container.Builder<V, ? extends Container<V, B, DB>, DB>, DB extends PorkDB> extends Logging {
    String getName();

    DB getDb();

    V getValue();

    void save() throws IOException;

    default boolean usesDirectory() {
        return true;
    }

    default void close() throws IOException {
        this.save();
    }

    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    @Accessors(chain = true)
    @Setter
    @Getter
    abstract class Builder<V, C extends Container<V, ? extends Builder<V, C, DB>, DB>, DB extends PorkDB> {
        @NonNull
        protected final DB db;

        @NonNull
        protected final String name;

        public abstract C buildIfPresent() throws IOException;

        public final C build() throws IOException {
            C built = this.buildImpl();
            this.db.getLoadedContainers().put(this.name, built);
            return built;
        }

        protected abstract C buildImpl() throws IOException;
    }
}
