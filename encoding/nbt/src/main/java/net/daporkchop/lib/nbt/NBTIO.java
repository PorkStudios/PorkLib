/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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
