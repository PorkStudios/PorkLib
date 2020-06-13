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

package net.daporkchop.lib.minecraft.format.java.section;

import net.daporkchop.lib.minecraft.format.common.DefaultSection;
import net.daporkchop.lib.minecraft.format.common.nibble.NibbleArray;
import net.daporkchop.lib.minecraft.format.common.storage.BlockStorage;
import net.daporkchop.lib.minecraft.version.MinecraftVersion;
import net.daporkchop.lib.unsafe.PUnsafe;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Extension of {@link DefaultSection} to implement the {@link JavaSection} interface.
 *
 * @author DaPorkchop_
 */
public class DefaultJavaSection extends DefaultSection implements JavaSection {
    protected static final long X_OFFSET = PUnsafe.pork_getOffset(DefaultSection.class, "x");
    protected static final long Z_OFFSET = PUnsafe.pork_getOffset(DefaultSection.class, "z");
    protected static final long XZ_STATUS_OFFSET = PUnsafe.pork_getOffset(DefaultJavaSection.class, "xzStatus");

    protected volatile int xzStatus = 0;

    public DefaultJavaSection(int y, BlockStorage blocks, NibbleArray blockLight, NibbleArray skyLight, MinecraftVersion version) {
        super(0, y, 0, blocks, blockLight, skyLight, version);
    }

    @Override
    public void _setXZ(int x, int z) {
        checkState(PUnsafe.compareAndSwapInt(this, XZ_STATUS_OFFSET, 0, 1), "XZ coordinates already set!");
        PUnsafe.putIntVolatile(this, X_OFFSET, x);
        PUnsafe.putIntVolatile(this, Z_OFFSET, z);
    }
}
