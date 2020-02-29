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

package net.daporkchop.lib.nbt;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.nbt.tag.TagRegistry;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Helper class for reading and writing NBT tags.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class NBTIO {
    private void verifyFileExists(@NonNull File file, boolean create) throws IOException {
        if (!file.exists()) {
            if (create) {
                File parent = file.getParentFile();
                if (!parent.exists() && !parent.mkdirs()) {
                    throw new IllegalStateException(String.format("Couldn't create directory: %s", parent.getAbsolutePath()));
                } else if (!file.createNewFile()) {
                    throw new IllegalStateException(String.format("Couldn't create file: %s", file.getAbsolutePath()));
                }
            } else {
                throw new IllegalStateException(String.format("File doesn't exist: %s", file.getAbsolutePath()));
            }
        }
    }

    public CompoundTag read(@NonNull InputStream inputStream) throws IOException {
        return read(inputStream, TagRegistry.NOTCHIAN);
    }

    public CompoundTag read(@NonNull InputStream inputStream, @NonNull TagRegistry registry) throws IOException {
        return new NBTInputStream(inputStream).readTag(registry);
    }

    public CompoundTag read(@NonNull File file) throws IOException {
        return read(file, TagRegistry.NOTCHIAN);
    }

    public CompoundTag read(@NonNull File file, @NonNull TagRegistry registry) throws IOException {
        verifyFileExists(file, false);
        try (NBTInputStream in = new NBTInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            return in.readTag(registry);
        }
    }

    public CompoundTag readGzipCompressed(@NonNull InputStream inputStream) throws IOException {
        return readGzipCompressed(inputStream, TagRegistry.NOTCHIAN);
    }

    public CompoundTag readGzipCompressed(@NonNull InputStream inputStream, @NonNull TagRegistry registry) throws IOException {
        return new NBTInputStream(new GZIPInputStream(inputStream)).readTag(registry);
    }

    public CompoundTag readGzipCompressed(@NonNull File file) throws IOException {
        return readGzipCompressed(file, TagRegistry.NOTCHIAN);
    }

    public CompoundTag readGzipCompressed(@NonNull File file, @NonNull TagRegistry registry) throws IOException {
        verifyFileExists(file, false);
        try (NBTInputStream in = new NBTInputStream(new GZIPInputStream(new FileInputStream(file)))) {
            return in.readTag(registry);
        }
    }

    public void write(@NonNull OutputStream out, @NonNull CompoundTag tag) throws IOException {
        write(out, tag, TagRegistry.NOTCHIAN);
    }

    public void write(@NonNull OutputStream out, @NonNull CompoundTag tag, @NonNull TagRegistry registry) throws IOException {
        NBTOutputStream nbtOut = new NBTOutputStream(out);
        nbtOut.writeTag(tag, registry);
    }

    public void write(@NonNull File file, @NonNull CompoundTag tag) throws IOException {
        write(file, tag, TagRegistry.NOTCHIAN);
    }

    public void write(@NonNull File file, @NonNull CompoundTag tag, @NonNull TagRegistry registry) throws IOException {
        verifyFileExists(file, true);
        try (NBTOutputStream out = new NBTOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            out.writeTag(tag, registry);
        }
    }

    public void writeGzipCompressed(@NonNull OutputStream out, @NonNull CompoundTag tag) throws IOException {
        write(out, tag, TagRegistry.NOTCHIAN);
    }

    public void writeGzipCompressed(@NonNull OutputStream out, @NonNull CompoundTag tag, @NonNull TagRegistry registry) throws IOException {
        NBTOutputStream nbtOut = new NBTOutputStream(new GZIPOutputStream(out));
        nbtOut.writeTag(tag, registry);
    }

    public void writeGzipCompressed(@NonNull File file, @NonNull CompoundTag tag) throws IOException {
        write(file, tag, TagRegistry.NOTCHIAN);
    }

    public void writeGzipCompressed(@NonNull File file, @NonNull CompoundTag tag, @NonNull TagRegistry registry) throws IOException {
        verifyFileExists(file, true);
        try (NBTOutputStream out = new NBTOutputStream(new GZIPOutputStream(new FileOutputStream(file)))) {
            out.writeTag(tag, registry);
        }
    }
}
