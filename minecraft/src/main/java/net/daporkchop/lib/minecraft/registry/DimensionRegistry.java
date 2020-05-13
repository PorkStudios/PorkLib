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

import lombok.NonNull;
import net.daporkchop.lib.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntConsumer;

import static net.daporkchop.lib.common.util.PValidation.checkArg;

/**
 * {@link Registry} implementation for dimensions.
 *
 * @author DaPorkchop_
 */
public class DimensionRegistry extends AbstractRegistry {
    public static final DimensionRegistry DEFAULT_JAVA = builder()
            .register(Identifier.fromString("minecraft:overworld"), 0)
            .register(Identifier.fromString("minecraft:nether"), -1)
            .register(Identifier.fromString("minecraft:end"), 1)
            .build();

    public static final DimensionRegistry DEFAULT_BEDROCK = builder()
            .register(Identifier.fromString("minecraft:overworld"), 0)
            .register(Identifier.fromString("minecraft:nether"), 1)
            .register(Identifier.fromString("minecraft:end"), 2)
            .build();

    public static Builder builder() {
        return new Builder();
    }

    protected final Map<Integer, Identifier> fromIds;

    protected DimensionRegistry(@NonNull AbstractRegistry.Builder builder) {
        super(builder, true, false);

        this.fromIds = new HashMap<>(builder.fromIds);
    }

    @Override
    public boolean contains(int id) {
        return this.fromIds.containsKey(id);
    }

    @Override
    public Identifier get(int id) {
        Identifier identifier = this.fromIds.get(id);
        checkArg(identifier != null, "Unknown ID: %s", id);
        return identifier;
    }

    @Override
    public void forEach(@NonNull IntConsumer action) {
        this.fromIds.keySet().forEach(action::accept);
    }

    public static class Builder extends AbstractRegistry.Builder {
        protected Builder() {
            super(Identifier.fromString("minecraft:blocks"));
        }

        @Override
        public Builder register(@NonNull Identifier identifier, int id) {
            super.register(identifier, id);
            return this;
        }

        @Override
        public synchronized DimensionRegistry build() {
            return new DimensionRegistry(this);
        }
    }
}
