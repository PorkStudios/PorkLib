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

package net.daporkchop.lib.db;

import lombok.NonNull;
import net.daporkchop.lib.binary.util.capability.Closeable;
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.lib.common.setting.Settings;
import net.daporkchop.lib.db.container.Container;
import net.daporkchop.lib.db.container.ContainerBuilder;
import net.daporkchop.lib.db.container.ContainerType;
import net.daporkchop.lib.db.engine.DBEngine;
import net.daporkchop.lib.db.util.exception.DBCloseException;
import net.daporkchop.lib.db.util.exception.DBOpenException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static net.daporkchop.lib.db.container.ContainerType.TYPE_COUNT;

/**
 * @author DaPorkchop_
 */
public class PorkDB implements Closeable<DBCloseException> {
    @SuppressWarnings("unchecked")
    protected final Map<String, Container>[] containerMaps = (Map<String, Container>[]) new Map[TYPE_COUNT];

    protected final DBEngine engine;

    public PorkDB(@NonNull DBEngine engine) {
        try {
            if (engine.isClosed()) {
                throw new IllegalStateException("Engine is already closed!");
            }

            this.engine = engine;
            this.engine.init(this);
            for (int i = TYPE_COUNT - 1; i >= 0; i--) {
                this.containerMaps[i] = new ConcurrentHashMap<>();
            }
        } catch (IOException e) {
            throw new DBOpenException(e);
        }
    }

    @Override
    public void close() {
            for (int i = TYPE_COUNT - 1; i >= 0; i--) {
                this.containerMaps[i].values().forEach(Container::close);
                this.containerMaps[i] = null;
            }
            this.engine.close();
    }

    @Override
    public boolean isClosed() {
        return this.engine.isClosed();
    }

    @SuppressWarnings("unchecked")
    public <C extends Container> C getContainer(@NonNull ContainerType<C> type, @NonNull String name, @NonNull Consumer<Settings> initializer)    {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name may not be empty!");
        }
        return (C) this.ensureOpen().containerMaps[type.getId()].computeIfAbsent(name, n -> {
            ContainerBuilder<C, ? extends DBEngine> builder = type.builder(this.engine.getTypeInfo(), this.engine)
                    .set(Container.NAME, n);
            initializer.accept(builder.getSettings());
            return builder.build();
        });
    }

    public PorkDB ensureOpen()    {
        if (this.isClosed())    {
            throw new IllegalStateException("Already closed!");
        } else {
            return this;
        }
    }

    public void closeContainer(@NonNull ContainerType type, @NonNull String name)   {
        Container container = this.ensureOpen().containerMaps[type.getId()].remove(name);
        if (container == null) {
            throw new IllegalStateException(String.format("Unknown container with id: %d:\"%s\"", type.getId(), name));
        } else if (container.isClosed()) {
            container.close();
        }
    }
}
