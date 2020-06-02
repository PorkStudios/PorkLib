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

package net.daporkchop.lib.minecraft.format.anvil;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.format.common.AbstractWorld;
import net.daporkchop.lib.minecraft.registry.BlockRegistry;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.world.WorldStorage;

import java.io.File;

/**
 * @author DaPorkchop_
 */
public class AnvilWorld extends AbstractWorld<AnvilSave, AnvilSaveOptions> {
    public AnvilWorld(@NonNull AnvilSave parent, @NonNull AnvilSaveOptions options, @NonNull Identifier id) {
        super(parent, options, id);
    }

    @Override
    protected BlockRegistry getBlockRegistry0() {
        return null;
    }

    @Override
    protected WorldStorage getStorage0() {
        int dimensionId = this.parent.dimensions().get(this.id);
        return new AnvilWorldStorage(dimensionId == 0 ? this.parent.root() : new File(this.parent.root(), "DIM" + dimensionId), this.options);
    }

    @Override
    public int layers() {
        return 1;
    }

    @Override
    public boolean hasSkyLight() {
        return false;
    }
}
