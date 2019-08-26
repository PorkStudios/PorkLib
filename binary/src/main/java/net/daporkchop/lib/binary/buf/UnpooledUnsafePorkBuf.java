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

package net.daporkchop.lib.binary.buf;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * An implementation of {@link PorkBuf} backed by direct memory.
 *
 * @author DaPorkchop_
 */
//TODO: see about adding a cleaner here (or force people to be responsible with their resources)
public final class UnpooledUnsafePorkBuf extends AbstractPorkBuf {
    private long addr;

    @Override
    protected void doRelease() {
        PUnsafe.freeMemory(this.addr);
    }

    @Override
    protected void doExpand(long oldCapacity, long newCapacity) {
        this.addr = PUnsafe.reallocateMemory(this.addr, newCapacity);
    }

    @Override
    public PorkBuf put(int b) {
        return null;
    }

    @Override
    public PorkBuf putShort(short s) {
        return null;
    }

    @Override
    public PorkBuf putShortLE(short s) {
        return null;
    }

    @Override
    public PorkBuf putInt(int i) {
        return null;
    }

    @Override
    public PorkBuf putIntLE(int i) {
        return null;
    }

    @Override
    public PorkBuf putLong(long i) {
        return null;
    }

    @Override
    public PorkBuf putLongLE(long i) {
        return null;
    }

    @Override
    public PorkBuf put(@NonNull byte[] b, int start, int len) {
        return null;
    }

    @Override
    public PorkBuf put(@NonNull ByteBuf src, int start, int count) {
        return null;
    }

    @Override
    public PorkBuf put(@NonNull ByteBuffer src, int start, int count) {
        return null;
    }

    @Override
    public long put(@NonNull ReadableByteChannel ch) throws IOException {
        return 0;
    }

    @Override
    public long put(@NonNull ReadableByteChannel ch, long max) throws IOException {
        return 0;
    }

    @Override
    public PorkBuf putAll(@NonNull ReadableByteChannel ch, long count) throws IOException {
        return null;
    }

    @Override
    public long put(@NonNull ScatteringByteChannel ch) throws IOException {
        return 0;
    }

    @Override
    public long put(@NonNull ScatteringByteChannel ch, long max) throws IOException {
        return 0;
    }

    @Override
    public PorkBuf putAll(@NonNull ScatteringByteChannel ch, long count) throws IOException {
        return null;
    }

    @Override
    public long put(@NonNull FileChannel ch) throws IOException {
        return 0;
    }

    @Override
    public long put(@NonNull FileChannel ch, long max) throws IOException {
        return 0;
    }

    @Override
    public long put(@NonNull FileChannel ch, long pos, long max) throws IOException {
        return 0;
    }

    @Override
    public PorkBuf putAll(@NonNull FileChannel ch, long count) throws IOException {
        return null;
    }

    @Override
    public PorkBuf putAll(@NonNull FileChannel ch, long pos, long count) throws IOException {
        return null;
    }

    @Override
    public long put(@NonNull InputStream in) throws IOException {
        return 0;
    }

    @Override
    public long put(@NonNull InputStream in, long max) throws IOException {
        return 0;
    }

    @Override
    public PorkBuf putAll(@NonNull InputStream in, long count) throws IOException {
        return null;
    }

    @Override
    public PorkBuf putZeroes(long count) {
        return null;
    }

    @Override
    public PorkBuf set(long index, int b) {
        return null;
    }

    @Override
    public PorkBuf setShort(long index, short s) {
        return null;
    }

    @Override
    public PorkBuf setShortLE(long index, short s) {
        return null;
    }

    @Override
    public PorkBuf setInt(long index, int i) {
        return null;
    }

    @Override
    public PorkBuf setIntLE(long index, int i) {
        return null;
    }

    @Override
    public PorkBuf setLong(long index, long i) {
        return null;
    }

    @Override
    public PorkBuf setLongLE(long index, long i) {
        return null;
    }

    @Override
    public PorkBuf set(long index, @NonNull byte[] b, int start, int len) {
        return null;
    }

    @Override
    public PorkBuf set(long index, @NonNull ByteBuf src, int start, int count) {
        return null;
    }

    @Override
    public PorkBuf set(long index, @NonNull ByteBuffer src, int start, int count) {
        return null;
    }

    @Override
    public long set(long index, @NonNull ReadableByteChannel ch) throws IOException {
        return 0;
    }

    @Override
    public long set(long index, @NonNull ReadableByteChannel ch, long max) throws IOException {
        return 0;
    }

    @Override
    public PorkBuf setAll(long index, @NonNull ReadableByteChannel ch, long count) throws IOException {
        return null;
    }

    @Override
    public long set(long index, @NonNull ScatteringByteChannel ch) throws IOException {
        return 0;
    }

    @Override
    public long set(long index, @NonNull ScatteringByteChannel ch, long max) throws IOException {
        return 0;
    }

    @Override
    public PorkBuf setAll(long index, @NonNull ScatteringByteChannel ch, long count) throws IOException {
        return null;
    }

    @Override
    public long set(long index, @NonNull FileChannel ch) throws IOException {
        return 0;
    }

    @Override
    public long set(long index, @NonNull FileChannel ch, long max) throws IOException {
        return 0;
    }

    @Override
    public long set(long index, @NonNull FileChannel ch, long pos, long max) throws IOException {
        return 0;
    }

    @Override
    public PorkBuf setAll(long index, @NonNull FileChannel ch, long count) throws IOException {
        return null;
    }

    @Override
    public PorkBuf setAll(long index, @NonNull FileChannel ch, long pos, long count) throws IOException {
        return null;
    }

    @Override
    public long set(long index, @NonNull InputStream in) throws IOException {
        return 0;
    }

    @Override
    public long set(long index, @NonNull InputStream in, long max) throws IOException {
        return 0;
    }

    @Override
    public PorkBuf setAll(long index, @NonNull InputStream in, long count) throws IOException {
        return null;
    }

    @Override
    public PorkBuf setZeroes(long index, long count) {
        return null;
    }

    @Override
    public int readByte() {
        return 0;
    }

    @Override
    public short readShort() {
        return 0;
    }

    @Override
    public short readShortLE() {
        return 0;
    }

    @Override
    public int readInt() {
        return 0;
    }

    @Override
    public int readIntLE() {
        return 0;
    }

    @Override
    public long readLong() {
        return 0;
    }

    @Override
    public long readLongLE() {
        return 0;
    }

    @Override
    public PorkBuf read(@NonNull byte[] b, int start, int len) {
        return null;
    }

    @Override
    public PorkBuf read(@NonNull ByteBuf dst) {
        return null;
    }

    @Override
    public PorkBuf read(@NonNull ByteBuf dst, int count) {
        return null;
    }

    @Override
    public PorkBuf read(@NonNull ByteBuf dst, int start, int count) {
        return null;
    }

    @Override
    public PorkBuf read(@NonNull ByteBuffer dst) {
        return null;
    }

    @Override
    public PorkBuf read(@NonNull ByteBuffer dst, int count) {
        return null;
    }

    @Override
    public PorkBuf read(@NonNull ByteBuffer src, int start, int count) {
        return null;
    }

    @Override
    public long read(@NonNull WritableByteChannel ch) throws IOException {
        return 0;
    }

    @Override
    public long read(@NonNull WritableByteChannel ch, long max) throws IOException {
        return 0;
    }

    @Override
    public PorkBuf readAll(@NonNull WritableByteChannel ch) throws IOException {
        return null;
    }

    @Override
    public PorkBuf readAll(@NonNull WritableByteChannel ch, long count) throws IOException {
        return null;
    }

    @Override
    public long read(@NonNull GatheringByteChannel ch) throws IOException {
        return 0;
    }

    @Override
    public long read(@NonNull GatheringByteChannel ch, long max) throws IOException {
        return 0;
    }

    @Override
    public PorkBuf readAll(@NonNull GatheringByteChannel ch) throws IOException {
        return null;
    }

    @Override
    public PorkBuf readAll(@NonNull GatheringByteChannel ch, long count) throws IOException {
        return null;
    }

    @Override
    public PorkBuf read(@NonNull FileChannel ch) throws IOException {
        return null;
    }

    @Override
    public PorkBuf read(@NonNull FileChannel ch, long count) throws IOException {
        return null;
    }

    @Override
    public PorkBuf read(@NonNull FileChannel ch, long pos, long count) throws IOException {
        return null;
    }

    @Override
    public PorkBuf read(@NonNull OutputStream out) throws IOException {
        return null;
    }

    @Override
    public PorkBuf read(@NonNull OutputStream out, long count) throws IOException {
        return null;
    }

    @Override
    public int getByte(long index) {
        return 0;
    }

    @Override
    public short getShort(long index) {
        return 0;
    }

    @Override
    public short getShortLE(long index) {
        return 0;
    }

    @Override
    public int getInt(long index) {
        return 0;
    }

    @Override
    public int getIntLE(long index) {
        return 0;
    }

    @Override
    public long getLong(long index) {
        return 0;
    }

    @Override
    public long getLongLE(long index) {
        return 0;
    }

    @Override
    public PorkBuf get(long index, @NonNull byte[] b, int start, int len) {
        return null;
    }

    @Override
    public PorkBuf get(long index, @NonNull ByteBuf dst) {
        return null;
    }

    @Override
    public PorkBuf get(long index, @NonNull ByteBuf dst, int count) {
        return null;
    }

    @Override
    public PorkBuf get(long index, @NonNull ByteBuf dst, int start, int count) {
        return null;
    }

    @Override
    public PorkBuf get(long index, @NonNull ByteBuffer dst) {
        return null;
    }

    @Override
    public PorkBuf get(long index, @NonNull ByteBuffer dst, int count) {
        return null;
    }

    @Override
    public PorkBuf get(long index, @NonNull ByteBuffer src, int start, int count) {
        return null;
    }

    @Override
    public long get(long index, @NonNull WritableByteChannel ch) throws IOException {
        return 0;
    }

    @Override
    public long get(long index, @NonNull WritableByteChannel ch, long max) throws IOException {
        return 0;
    }

    @Override
    public PorkBuf getAll(long index, @NonNull WritableByteChannel ch) throws IOException {
        return null;
    }

    @Override
    public PorkBuf getAll(long index, @NonNull WritableByteChannel ch, long count) throws IOException {
        return null;
    }

    @Override
    public long get(long index, @NonNull GatheringByteChannel ch) throws IOException {
        return 0;
    }

    @Override
    public long get(long index, @NonNull GatheringByteChannel ch, long max) throws IOException {
        return 0;
    }

    @Override
    public PorkBuf getAll(long index, @NonNull GatheringByteChannel ch) throws IOException {
        return null;
    }

    @Override
    public PorkBuf getAll(long index, @NonNull GatheringByteChannel ch, long count) throws IOException {
        return null;
    }

    @Override
    public PorkBuf get(long index, @NonNull FileChannel ch) throws IOException {
        return null;
    }

    @Override
    public PorkBuf get(long index, @NonNull FileChannel ch, long count) throws IOException {
        return null;
    }

    @Override
    public PorkBuf get(long index, @NonNull FileChannel ch, long pos, long count) throws IOException {
        return null;
    }

    @Override
    public PorkBuf get(long index, @NonNull OutputStream out) throws IOException {
        return null;
    }

    @Override
    public PorkBuf get(long index, @NonNull OutputStream out, long count) throws IOException {
        return null;
    }
}
