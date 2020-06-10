/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.minecraft.registry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.primitive.map.IntObjMap;
import net.daporkchop.lib.primitive.map.ObjIntMap;
import net.daporkchop.lib.primitive.map.open.IntObjOpenHashMap;
import net.daporkchop.lib.primitive.map.open.ObjIntOpenHashMap;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.ObjIntConsumer;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * A basic implementation of a {@link Registry}.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public abstract class AbstractRegistry implements Registry {
    protected final ObjIntMap<Identifier> toIds;
    protected final Identifier[] fromIds;

    @Getter
    protected final Identifier id;

    protected AbstractRegistry(@NonNull Builder builder, boolean toIds, boolean fromIds) {
        this.id = builder.id;
        if (toIds)   {
            this.toIds = new ObjIntOpenHashMap<>();
            builder.toIds.forEach(this.toIds::put);
        } else {
            this.toIds = null;
        }
        if (fromIds) {
            this.fromIds = new Identifier[builder.highestId + 1];
            builder.toIds.forEach((identifier, id) -> this.fromIds[id] = identifier);
        } else {
            this.fromIds = null;
        }
    }

    @Override
    public int size() {
        return this.toIds.size();
    }

    @Override
    public boolean contains(@NonNull Identifier identifier) {
        return this.toIds.containsKey(identifier);
    }

    @Override
    public boolean contains(int id) {
        return id >= 0 && id < this.fromIds.length && this.fromIds[id] != null;
    }

    @Override
    public int get(@NonNull Identifier identifier) {
        int id = this.toIds.getOrDefault(identifier, -1);
        checkArg(id >= 0, "Unknown ID: %s", identifier);
        return id;
    }

    @Override
    public Identifier get(int id) {
        Identifier identifier = id >= 0 && id < this.fromIds.length ? this.fromIds[id] : null;
        checkArg(identifier != null, "Unknown ID: %s", id);
        return identifier;
    }

    @Override
    public Iterator<Identifier> iterator() {
        return this.toIds.keySet().iterator();
    }

    @Override
    public void forEach(@NonNull Consumer<? super Identifier> action) {
        this.toIds.keySet().forEach(action);
    }

    @Override
    public void forEach(@NonNull IntConsumer action) {
        for (int i = 0, length = this.fromIds.length; i < length; i++)  {
            if (this.fromIds[i] != null)    {
                action.accept(i);
            }
        }
    }

    @Override
    public void forEach(@NonNull ObjIntConsumer<? super Identifier> action) {
        this.toIds.forEach(action::accept);
    }

    @RequiredArgsConstructor
    public static abstract class Builder {
        @NonNull
        protected final Identifier id;

        protected final ObjIntMap<Identifier> toIds = new ObjIntOpenHashMap<>();
        protected final IntObjMap<Identifier> fromIds = new IntObjOpenHashMap<>();

        protected int highestId = 0;

        /**
         * Registers the given {@link Identifier} to numeric ID pair.
         *
         * @param identifier the {@link Identifier}
         * @param id         the numeric ID
         * @return this builder
         */
        protected synchronized Builder register(@NonNull Identifier identifier, int id) {
            checkState(!this.toIds.containsKey(identifier), "ID %s is already registered!", identifier);
            checkState(!this.fromIds.containsKey(id), "ID %s is already registered!", id); //use %s to avoid having a second string instance, the result should be the same
            if (id > this.highestId) {
                this.highestId = id;
            }
            this.toIds.put(identifier, id);
            this.fromIds.put(id, identifier);
            return this;
        }

        /**
         * Builds the completed registry.
         *
         * @return the completed registry
         */
        public abstract Registry build();
    }
}
