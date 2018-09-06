/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

import net.daporkchop.lib.nbt.stream.NBTInputStream;
import net.daporkchop.lib.nbt.stream.NBTOutputStream;
import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.lib.nbt.tag.impl.notch.CompoundTag;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NBTIO {

    public static CompoundTag read(File file) {
        return read(file, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag read(File file, ByteOrder endianness) {
        try {
            if (!file.exists()) return null;
            return read(new FileInputStream(file), endianness);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static CompoundTag read(InputStream inputStream) {
        return read(inputStream, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag read(InputStream inputStream, ByteOrder endianness) {
        return read(inputStream, endianness, false);
    }

    public static CompoundTag read(InputStream inputStream, ByteOrder endianness, boolean network) {
        try {
            try (NBTInputStream stream = new NBTInputStream(inputStream, endianness, network)) {
                Tag tag = Tag.readNamedTag(stream);
                inputStream.close();
                if (tag instanceof CompoundTag) {
                    return (CompoundTag) tag;
                }
                throw new IOException("Root tag must be a named compound tag");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static CompoundTag read(byte[] data) {
        return read(data, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag read(byte[] data, ByteOrder endianness) {
        return read(new ByteArrayInputStream(data), endianness);
    }

    public static CompoundTag read(byte[] data, ByteOrder endianness, boolean network) {
        return read(new ByteArrayInputStream(data), endianness, network);
    }

    public static CompoundTag readCompressed(InputStream inputStream) {
        return readCompressed(inputStream, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readCompressed(InputStream inputStream, ByteOrder endianness) {
        try {
            return read(new BufferedInputStream(new GZIPInputStream(inputStream)), endianness);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static CompoundTag readCompressed(byte[] data) {
        return readCompressed(data, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readCompressed(byte[] data, ByteOrder endianness) {
        try {
            return read(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(data))), endianness, true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static CompoundTag readNetworkCompressed(InputStream inputStream) {
        return readNetworkCompressed(inputStream, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readNetworkCompressed(InputStream inputStream, ByteOrder endianness) {
        try {
            return read(new BufferedInputStream(new GZIPInputStream(inputStream)), endianness);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static CompoundTag readNetworkCompressed(byte[] data) {
        return readNetworkCompressed(data, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readNetworkCompressed(byte[] data, ByteOrder endianness) {
        try {
            return read(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(data))), endianness, true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static byte[] write(CompoundTag tag) {
        return write(tag, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] write(CompoundTag tag, ByteOrder endianness) {
        return write(tag, endianness, false);
    }

    public static byte[] write(CompoundTag tag, ByteOrder endianness, boolean network) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (NBTOutputStream stream = new NBTOutputStream(baos, endianness, network)) {
                Tag.writeNamedTag(tag, stream);
                return baos.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static byte[] write(Collection<CompoundTag> tags) {
        return write(tags, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] write(Collection<CompoundTag> tags, ByteOrder endianness) {
        return write(tags, endianness, false);
    }

    public static byte[] write(Collection<CompoundTag> tags, ByteOrder endianness, boolean network) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (NBTOutputStream stream = new NBTOutputStream(baos, endianness, network)) {
                for (CompoundTag tag : tags) {
                    Tag.writeNamedTag(tag, stream);
                }
                return baos.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static void write(CompoundTag tag, File file) {
        write(tag, file, ByteOrder.BIG_ENDIAN);
    }

    public static void write(CompoundTag tag, File file, ByteOrder endianness) {
        try {
            write(tag, new FileOutputStream(file), endianness);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static void write(CompoundTag tag, OutputStream outputStream) {
        write(tag, outputStream, ByteOrder.BIG_ENDIAN);
    }

    public static void write(CompoundTag tag, OutputStream outputStream, ByteOrder endianness) {
        write(tag, outputStream, endianness, false);
    }

    public static void write(CompoundTag tag, OutputStream outputStream, ByteOrder endianness, boolean network) {
        try {
            try (NBTOutputStream stream = new NBTOutputStream(outputStream, endianness, network)) {
                Tag.writeNamedTag(tag, stream);
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static byte[] writeGZIPCompressed(CompoundTag tag) {
        return writeGZIPCompressed(tag, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] writeGZIPCompressed(CompoundTag tag, ByteOrder endianness) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeGZIPCompressed(tag, baos, endianness);
        return baos.toByteArray();
    }

    public static void writeGZIPCompressed(CompoundTag tag, OutputStream outputStream) {
        writeGZIPCompressed(tag, outputStream, ByteOrder.BIG_ENDIAN);
    }

    public static void writeGZIPCompressed(CompoundTag tag, OutputStream outputStream, ByteOrder endianness) {
        try {
            write(tag, new GZIPOutputStream(outputStream), endianness);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static byte[] writeNetworkGZIPCompressed(CompoundTag tag) {
        return writeNetworkGZIPCompressed(tag, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] writeNetworkGZIPCompressed(CompoundTag tag, ByteOrder endianness) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeNetworkGZIPCompressed(tag, baos, endianness);
        return baos.toByteArray();
    }

    public static void writeNetworkGZIPCompressed(CompoundTag tag, OutputStream outputStream) {
        writeNetworkGZIPCompressed(tag, outputStream, ByteOrder.BIG_ENDIAN);
    }

    public static void writeNetworkGZIPCompressed(CompoundTag tag, OutputStream outputStream, ByteOrder endianness) {
        try {
            write(tag, new GZIPOutputStream(outputStream), endianness, true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static byte[] writeZLIBCompressed(CompoundTag tag) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeZLIBCompressed(tag, baos);
        return baos.toByteArray();
    }

    public static void writeZLIBCompressed(CompoundTag tag, OutputStream outputStream) {
        writeZLIBCompressed(tag, outputStream, ByteOrder.BIG_ENDIAN);
    }

    public static void writeZLIBCompressed(CompoundTag tag, OutputStream outputStream, ByteOrder endianness) {
        writeZLIBCompressed(tag, outputStream, Deflater.DEFAULT_COMPRESSION, endianness);
    }

    public static void writeZLIBCompressed(CompoundTag tag, OutputStream outputStream, int level) {
        writeZLIBCompressed(tag, outputStream, level, ByteOrder.BIG_ENDIAN);
    }

    public static void writeZLIBCompressed(CompoundTag tag, OutputStream outputStream, int level, ByteOrder endianness) {
        write(tag, new DeflaterOutputStream(outputStream, new Deflater(level)), endianness);
    }

    public static void safeWrite(CompoundTag tag, File file) {
        try {
            File tmpFile = new File(file.getAbsolutePath() + "_tmp");
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
            write(tag, tmpFile);
            Files.move(tmpFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

}
