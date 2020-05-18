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

package net.daporkchop.lib.minecraft.format.anvil.region.impl;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.common.util.exception.ReadOnlyException;
import net.daporkchop.lib.minecraft.format.anvil.region.RawChunk;
import net.daporkchop.lib.minecraft.format.anvil.region.RegionConstants;
import net.daporkchop.lib.minecraft.format.anvil.region.RegionFile;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.File;
import java.io.IOException;

/**
 * A dummy implementation of {@link RegionFile} which is empty and contains no chunks.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmptyRegionFile implements RegionFile {
    public static final EmptyRegionFile INSTANCE = new EmptyRegionFile();

    @Override
    public RawChunk read(int x, int z) throws IOException {
        RegionConstants.checkCoords(x, z);
        return null;
    }

    @Override
    public boolean write(int x, int z, @NonNull ByteBuf data, int version, long timestamp, boolean forceOverwrite) throws ReadOnlyException, IOException {
        throw new ReadOnlyException();
    }

    @Override
    public boolean delete(int x, int z) throws ReadOnlyException, IOException {
        throw new ReadOnlyException();
    }

    @Override
    public boolean contains(int x, int z) throws IOException {
        RegionConstants.checkCoords(x, z);
        return false;
    }

    @Override
    public long timestamp(int x, int z) throws IOException {
        return -1;
    }

    @Override
    public void defrag() throws ReadOnlyException, IOException {
        throw new ReadOnlyException();
    }

    @Override
    public File file() {
        return null;
    }

    @Override
    public boolean readOnly() {
        return true;
    }

    @Override
    public void flush() throws IOException {
        //no-op
    }

    @Override
    public void close() throws IOException {
        //no-op
    }
}
