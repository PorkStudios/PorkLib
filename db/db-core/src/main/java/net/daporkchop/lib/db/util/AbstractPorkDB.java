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

package net.daporkchop.lib.db.util;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.db.ContainerFactory;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.db.builder.AbstractDBBuilder;
import net.daporkchop.lib.db.container.Container;
import net.daporkchop.lib.db.container.ContainerType;
import net.daporkchop.lib.db.util.exception.DBCloseException;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author DaPorkchop_
 */
@Getter
public abstract class AbstractPorkDB<F extends ContainerFactory, Impl extends AbstractPorkDB<F, Impl>> implements PorkDB<F> {
    protected final F factory;
    protected final Map<ContainerType, Map<String, Container>> typeMap;
    protected final AtomicBoolean closed = new AtomicBoolean(false);

    public AbstractPorkDB(@NonNull AbstractDBBuilder<Impl, ? extends AbstractDBBuilder> builder, @NonNull F factory) {
        builder.validate();

        this.factory = factory;

        this.typeMap = builder.getTypeMapSupplier().get();
        for (ContainerType type : ContainerType.values()) {
            this.typeMap.put(type, builder.getContainerMapSupplier().apply(type));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends Container> C getContainer(@NonNull ContainerType type, @NonNull String name) {
        this.ensureOpen();
        Map<String, Container> containerMap = this.typeMap.get(type);
        if (containerMap == null)   {
            //in theory the database could be closed in the time it takes to call #get(), but let's ignore that edge case because it's so utterly unlikely
            throw new IllegalStateException(String.format("Couldn't find container map for type: %s!", type));
        }
        return (C) containerMap.get(name);
    }

    @Override
    public void closeContainer(@NonNull ContainerType type, @NonNull String name) throws DBCloseException {
        this.ensureOpen();
        Map<String, Container> containerMap = this.typeMap.get(type);
        if (containerMap == null)   {
            //in theory the database could be closed in the time it takes to call #get(), but let's ignore that edge case because it's so utterly unlikely
            throw new IllegalStateException(String.format("Couldn't find container map for type: %s!", type));
        }
        Container container = containerMap.remove(name);
        if (container == null)  {
            throw new IllegalStateException(String.format("Couldn't find container with type %s and name \"%s\"!", type, name));
        }
        container.close();
    }

    @Override
    public final void close() throws DBCloseException {
        if (!this.closed.getAndSet(true)) {
            try {
                this.doPreClose();
                for (ContainerType type : ContainerType.values()) {
                    Map<String, Container> map = this.typeMap.remove(type);
                    if (map == null) {
                        throw new IllegalStateException(String.format("No map for container type: %s!", type));
                    }
                    for (Iterator<Container> iterator = map.values().iterator(); iterator.hasNext(); ) {
                        Container container = iterator.next();
                        iterator.remove();
                        container.close();
                    }
                    if (!map.isEmpty()) {
                        throw new ConcurrentModificationException("Impossible state: name -> container map wasn't empty after clearing!");
                    }
                }
                this.doPostClose();
            } catch (Exception e)   {
                throw new DBCloseException(e);
            }
        }
    }

    protected abstract void doPreClose() throws IOException;
    protected abstract void doPostClose() throws IOException;

    @Override
    public boolean isClosed() {
        return this.closed.get();
    }
}
