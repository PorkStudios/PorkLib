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

package net.daporkchop.lib.minecraft.tileentity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.registry.ResourceLocation;
import net.daporkchop.lib.minecraft.tileentity.impl.TileEntitySign;
import net.daporkchop.lib.minecraft.tileentity.impl.UnknownTileEntity;
import net.daporkchop.lib.minecraft.util.factory.TileEntityFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A registry for tile entities.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TileEntityRegistry implements TileEntityFactory {
    protected static TileEntityRegistry DEFAULT = null;

    /**
     * @return a new {@link Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new {@link Builder} using the given {@link TileEntityRegistry} instance as a template.
     *
     * @param source the template {@link TileEntityRegistry} to use
     * @return a new {@link Builder} instance
     */
    public static Builder builder(@NonNull TileEntityRegistry source) {
        return new Builder().putAll(source).fallback(source.fallback);
    }

    /**
     * Gets a default {@link TileEntityRegistry} instance to be used when the user does not provide one.
     *
     * @return a default {@link TileEntityRegistry}
     */
    public synchronized static TileEntityRegistry defaultRegistry() {
        TileEntityRegistry registry = DEFAULT;
        if (registry == null) {
            DEFAULT = registry = builder().fallback(UnknownTileEntity::new)
                    .put(TileEntitySign.ID, TileEntitySign::new)
                    .build();
        }
        return registry;
    }

    @NonNull
    protected final Map<ResourceLocation, Supplier<? extends TileEntity>> delegate;
    protected final Supplier<? extends TileEntity>                        fallback;

    @Override
    public TileEntity create(@NonNull ResourceLocation id) {
        Supplier<? extends TileEntity> supplier = this.delegate.getOrDefault(id, this.fallback);
        if (supplier != null) {
            return supplier.get();
        } else {
            throw new IllegalArgumentException(String.format("ID \"%s\" not registered!", id));
        }
    }

    /**
     * Builder for {@link TileEntityRegistry}.
     *
     * @author DaPorkchop_
     */
    @Accessors(fluent = true, chain = true)
    public static class Builder {
        protected Map<ResourceLocation, Supplier<? extends TileEntity>> map = Collections.emptyMap();
        @Getter
        @Setter
        protected Supplier<? extends TileEntity> fallback;

        /**
         * Adds a new tile entity using the given {@link ResourceLocation} ID and a supplier of instances.
         *
         * @param id       the tile entity's ID
         * @param supplier an {@link Supplier} for creating instances of the tile entity
         * @return this {@link Builder} instance
         * @throws IllegalArgumentException if the given ID is already registered
         */
        public synchronized Builder add(@NonNull ResourceLocation id, @NonNull Supplier<? extends TileEntity> supplier) {
            if (this.map.isEmpty()) {
                this.map = new HashMap<>();
            }
            if (this.map.putIfAbsent(id, supplier) != null) {
                throw new IllegalArgumentException(String.format("ID \"%s\" already registered!", id));
            }
            return this;
        }

        /**
         * Adds a new tile entity using the given ID and a supplier of instances.
         *
         * @param id       the tile entity's ID
         * @param supplier an {@link Supplier} for creating instances of the tile entity
         * @return this {@link Builder} instance
         * @throws IllegalArgumentException if the given ID is already registered
         */
        public synchronized Builder add(@NonNull String id, @NonNull Supplier<? extends TileEntity> supplier) {
            if (this.map.isEmpty()) {
                this.map = new HashMap<>();
            }
            if (this.map.putIfAbsent(new ResourceLocation(id), supplier) != null) {
                throw new IllegalArgumentException(String.format("ID \"%s\" already registered!", id));
            }
            return this;
        }

        /**
         * Adds all registered tile entities from the given {@link Map}.
         *
         * @param source the {@link Map} from which to copy registrations
         * @return this {@link Builder} instance
         * @see #add(ResourceLocation, Supplier)
         */
        public synchronized Builder addAll(@NonNull Map<ResourceLocation, Supplier<? extends TileEntity>> source) {
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
         * Adds all registered tile entities from the given {@link Builder}.
         *
         * @param source the {@link Builder} from which to copy registrations
         * @return this {@link Builder} instance
         * @see #add(ResourceLocation, Supplier)
         */
        public synchronized Builder addAll(@NonNull Builder source) {
            return this.addAll(source.map);
        }

        /**
         * Adds all registered tile entities from the given {@link TileEntityRegistry}.
         *
         * @param source the {@link TileEntityRegistry} from which to copy registrations
         * @return this {@link Builder} instance
         * @see #add(ResourceLocation, Supplier)
         */
        public synchronized Builder addAll(@NonNull TileEntityRegistry source) {
            return this.addAll(source.delegate);
        }

        /**
         * Adds a new tile entity using the given {@link ResourceLocation} ID and a supplier of instances.
         * <p>
         * If the given ID is already registered in this builder, it will be silently replaced.
         *
         * @param id       the tile entity's ID
         * @param supplier an {@link Supplier} for creating instances of the tile entity
         * @return this {@link Builder} instance
         */
        public synchronized Builder put(@NonNull ResourceLocation id, @NonNull Supplier<? extends TileEntity> supplier) {
            if (this.map.isEmpty()) {
                this.map = new HashMap<>();
            }
            this.map.put(id, supplier);
            return this;
        }

        /**
         * Adds a new tile entity using the given ID and a supplier of instances.
         * <p>
         * If the given ID is already registered in this builder, it will be silently replaced.
         *
         * @param id       the tile entity's ID
         * @param supplier an {@link Supplier} for creating instances of the tile entity
         * @return this {@link Builder} instance
         */
        public synchronized Builder put(@NonNull String id, @NonNull Supplier<? extends TileEntity> supplier) {
            if (this.map.isEmpty()) {
                this.map = new HashMap<>();
            }
            this.map.put(new ResourceLocation(id), supplier);
            return this;
        }

        /**
         * Adds all registered tile entities from the given {@link Map}.
         * <p>
         * Any IDs which are already registered in this builder will be silently replaced.
         *
         * @param source the {@link Map} from which to copy registrations
         * @return this {@link Builder} instance
         * @see #put(ResourceLocation, Supplier)
         */
        public synchronized Builder putAll(@NonNull Map<ResourceLocation, Supplier<? extends TileEntity>> source) {
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
         * Adds all registered tile entities from the given {@link Builder}.
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
         * Adds all registered tile entities from the given {@link TileEntityRegistry}.
         * <p>
         * Any IDs which are already registered in this builder will be silently replaced.
         *
         * @param source the {@link TileEntityRegistry} from which to copy registrations
         * @return this {@link Builder} instance
         * @see #put(ResourceLocation, Supplier)
         */
        public synchronized Builder putAll(@NonNull TileEntityRegistry source) {
            return this.addAll(source.delegate);
        }

        /**
         * Creates a new {@link TileEntityRegistry} using the currently configured settings.
         *
         * @return a new {@link TileEntityRegistry}
         */
        public synchronized TileEntityRegistry build() {
            TileEntityRegistry registry = new TileEntityRegistry(this.map, this.fallback);
            this.map = Collections.emptyMap();
            return registry;
        }
    }
}
