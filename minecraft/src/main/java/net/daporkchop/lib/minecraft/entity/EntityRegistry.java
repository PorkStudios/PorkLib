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

package net.daporkchop.lib.minecraft.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.entity.impl.UnknownEntity;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.util.factory.EntityFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A registry for entities.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class EntityRegistry implements EntityFactory {
    protected static EntityRegistry DEFAULT = null;

    /**
     * @return a new {@link Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new {@link Builder} using the given {@link EntityRegistry} instance as a template.
     *
     * @param source the template {@link EntityRegistry} to use
     * @return a new {@link Builder} instance
     */
    public static Builder builder(@NonNull EntityRegistry source) {
        return new Builder().putAll(source).fallback(source.fallback);
    }

    /**
     * Gets a default {@link EntityRegistry} instance to be used when the user does not provide one.
     *
     * @return a default {@link EntityRegistry}
     */
    public synchronized static EntityRegistry defaultRegistry() {
        EntityRegistry registry = DEFAULT;
        if (registry == null) {
            DEFAULT = registry = builder().fallback(UnknownEntity::new)
                    .build();
        }
        return registry;
    }

    @NonNull
    protected final Map<ResourceLocation, Supplier<? extends Entity>> delegate;
    protected final Supplier<? extends Entity> fallback;

    @Override
    public Entity create(@NonNull ResourceLocation id) {
        Supplier<? extends Entity> supplier = this.delegate.getOrDefault(id, this.fallback);
        if (supplier != null) {
            return supplier.get();
        } else {
            throw new IllegalArgumentException(String.format("ID \"%s\" not registered!", id));
        }
    }

    /**
     * Builder for {@link EntityRegistry}.
     *
     * @author DaPorkchop_
     */
    @Accessors(fluent = true, chain = true)
    public static class Builder {
        protected Map<ResourceLocation, Supplier<? extends Entity>> map = Collections.emptyMap();
        @Getter
        @Setter
        protected Supplier<? extends Entity> fallback;

        /**
         * Adds a new entity using the given {@link ResourceLocation} ID and a supplier of instances.
         *
         * @param id       the entity's ID
         * @param supplier an {@link Supplier} for creating instances of the entity
         * @return this {@link Builder} instance
         * @throws IllegalArgumentException if the given ID is already registered
         */
        public synchronized Builder add(@NonNull ResourceLocation id, @NonNull Supplier<? extends Entity> supplier) {
            if (this.map.isEmpty()) {
                this.map = new HashMap<>();
            }
            if (this.map.putIfAbsent(id, supplier) != null) {
                throw new IllegalArgumentException(String.format("ID \"%s\" already registered!", id));
            }
            return this;
        }

        /**
         * Adds a new entity using the given ID and a supplier of instances.
         *
         * @param id       the entity's ID
         * @param supplier an {@link Supplier} for creating instances of the entity
         * @return this {@link Builder} instance
         * @throws IllegalArgumentException if the given ID is already registered
         */
        public synchronized Builder add(@NonNull String id, @NonNull Supplier<? extends Entity> supplier) {
            if (this.map.isEmpty()) {
                this.map = new HashMap<>();
            }
            if (this.map.putIfAbsent(new ResourceLocation(id), supplier) != null) {
                throw new IllegalArgumentException(String.format("ID \"%s\" already registered!", id));
            }
            return this;
        }

        /**
         * Adds all registered entities from the given {@link Map}.
         *
         * @param source the {@link Map} from which to copy registrations
         * @return this {@link Builder} instance
         * @see #add(ResourceLocation, Supplier)
         */
        public synchronized Builder addAll(@NonNull Map<ResourceLocation, Supplier<? extends Entity>> source) {
            if (!source.isEmpty()) {
                if (this.map.isEmpty()) {
                    this.map = new HashMap<>(source);
                } else {
                    //check if any keys already exist in the map
                    source.forEach((id, supplier) -> {
                        if (this.map.containsKey(id)) {
                            throw new IllegalArgumentException(String.format("ID \"%s\" already registered!", id));
                        }
                    });

                    this.map.putAll(source);
                }
            }
            return this;
        }

        /**
         * Adds all registered entities from the given {@link Builder}.
         *
         * @param source the {@link Builder} from which to copy registrations
         * @return this {@link Builder} instance
         * @see #add(ResourceLocation, Supplier)
         */
        public synchronized Builder addAll(@NonNull Builder source) {
            return this.addAll(source.map);
        }

        /**
         * Adds all registered entities from the given {@link EntityRegistry}.
         *
         * @param source the {@link EntityRegistry} from which to copy registrations
         * @return this {@link Builder} instance
         * @see #add(ResourceLocation, Supplier)
         */
        public synchronized Builder addAll(@NonNull EntityRegistry source) {
            return this.addAll(source.delegate);
        }

        /**
         * Adds a new entity using the given {@link ResourceLocation} ID and a supplier of instances.
         * <p>
         * If the given ID is already registered in this builder, it will be silently replaced.
         *
         * @param id       the entity's ID
         * @param supplier an {@link Supplier} for creating instances of the entity
         * @return this {@link Builder} instance
         */
        public synchronized Builder put(@NonNull ResourceLocation id, @NonNull Supplier<? extends Entity> supplier) {
            if (this.map.isEmpty()) {
                this.map = new HashMap<>();
            }
            this.map.put(id, supplier);
            return this;
        }

        /**
         * Adds a new entity using the given ID and a supplier of instances.
         * <p>
         * If the given ID is already registered in this builder, it will be silently replaced.
         *
         * @param id       the entity's ID
         * @param supplier an {@link Supplier} for creating instances of the entity
         * @return this {@link Builder} instance
         */
        public synchronized Builder put(@NonNull String id, @NonNull Supplier<? extends Entity> supplier) {
            if (this.map.isEmpty()) {
                this.map = new HashMap<>();
            }
            this.map.put(new ResourceLocation(id), supplier);
            return this;
        }

        /**
         * Adds all registered entities from the given {@link Map}.
         * <p>
         * Any IDs which are already registered in this builder will be silently replaced.
         *
         * @param source the {@link Map} from which to copy registrations
         * @return this {@link Builder} instance
         * @see #put(ResourceLocation, Supplier)
         */
        public synchronized Builder putAll(@NonNull Map<ResourceLocation, Supplier<? extends Entity>> source) {
            if (!source.isEmpty()) {
                if (this.map.isEmpty()) {
                    this.map = new HashMap<>(source);
                } else {
                    this.map.putAll(source);
                }
            }
            return this;
        }

        /**
         * Adds all registered entities from the given {@link Builder}.
         * <p>
         * Any IDs which are already registered in this builder will be silently replaced.
         *
         * @param source the {@link Builder} from which to copy registrations
         * @return this {@link Builder} instance
         * @see #put(ResourceLocation, Supplier)
         */
        public synchronized Builder putAll(@NonNull Builder source) {
            return this.addAll(source.map);
        }

        /**
         * Adds all registered entities from the given {@link EntityRegistry}.
         * <p>
         * Any IDs which are already registered in this builder will be silently replaced.
         *
         * @param source the {@link EntityRegistry} from which to copy registrations
         * @return this {@link Builder} instance
         * @see #put(ResourceLocation, Supplier)
         */
        public synchronized Builder putAll(@NonNull EntityRegistry source) {
            return this.addAll(source.delegate);
        }

        /**
         * Creates a new {@link EntityRegistry} using the currently configured settings.
         *
         * @return a new {@link EntityRegistry}
         */
        public synchronized EntityRegistry build() {
            EntityRegistry registry = new EntityRegistry(this.map, this.fallback);
            this.map = Collections.emptyMap();
            return registry;
        }
    }
}
