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
import net.daporkchop.lib.primitive.map.ObjIntMap;
import net.daporkchop.lib.primitive.map.hash.open.ObjIntOpenHashMap;

/**
 * An extension of {@link IDRegistryImpl} that stores {@link Identifier} to ID mappings in a map for fast reverse lookups.
 *
 * @author DaPorkchop_
 */
public class MappedIDRegistryImpl extends IDRegistryImpl {
    protected final ObjIntMap<Identifier> map = new ObjIntOpenHashMap<>();

    public MappedIDRegistryImpl(@NonNull Identifier[] contents, Identifier name) {
        super(contents, name);

        this.forEach(this.map::put);
    }

    @Override
    public int lookup(@NonNull Identifier name) {
        return this.map.get(name);
    }
}
