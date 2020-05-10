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

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Base implementation of a {@link Registry}.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public abstract class AbstractRegistry implements Registry {
    protected final Map<Identifier, Integer> toIds;
    protected final Identifier[] fromIds;

    @Getter
    protected final Identifier id;

    public AbstractRegistry(@NonNull Builder builder) {
        this.id = builder.id;
        this.toIds = new IdentityHashMap<>(builder.toIds);
        this.fromIds = new Identifier[builder.highestId];
        this.toIds.forEach((identifier, id) -> this.fromIds[id] = identifier);
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
    public int get(@NonNull Identifier identifier) {
        Integer id = this.toIds.get(identifier);
        checkArg(id != null, "Unknown ID: %s", identifier);
        return id;
    }

    @Override
    public int get(@NonNull Identifier identifier, int fallback) {
        Integer id = this.toIds.get(identifier);
        return id != null ? id : fallback;
    }

    @Override
    public boolean contains(int id) {
        return id >= 0 && id < this.fromIds.length && this.fromIds[id] != null;
    }

    @Override
    public Identifier get(int id) {
        Identifier identifier = id >= 0 && id < this.fromIds.length ? this.fromIds[id] : null;
        checkArg(identifier != null, "Unknown ID: %s", id);
        return identifier;
    }

    @Override
    public Identifier get(int id, Identifier fallback) {
        Identifier identifier = id >= 0 && id < this.fromIds.length ? this.fromIds[id] : null;
        return identifier != null ? identifier : fallback;
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
    public void forEach(@NonNull ObjIntConsumer<Identifier> action) {
        this.toIds.forEach(action::accept);
    }

    @RequiredArgsConstructor
    public static abstract class Builder {
        @NonNull
        protected final Identifier id;

        protected final Map<Identifier, Integer> toIds = new IdentityHashMap<>();
        protected final Map<Integer, Identifier> fromIds = new HashMap<>();

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
