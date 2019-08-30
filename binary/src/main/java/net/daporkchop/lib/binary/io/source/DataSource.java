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

package net.daporkchop.lib.binary.io.source;

import lombok.NonNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Extension on top of {@link ByteSource} which allows for reading higher-level data types (such as primitives or
 * UTF-8 text).
 *
 * @author DaPorkchop_
 */
public interface DataSource extends ByteSource {
    /**
     * Gets the next boolean from this source.
     *
     * @return the next boolean
     * @see #next()
     */
    default boolean nextBoolean() throws IOException {
        return this.next() != 0;
    }

    /**
     * Gets the next signed big-endian short from this source.
     *
     * @return the next signed big-endian short
     * @see #next()
     */
    default short nextShort() throws IOException {
        return (short) this.nextUShort();
    }

    /**
     * Gets the next signed little-endian short from this source.
     *
     * @return the next signed little-endian short
     * @see #next()
     */
    default short nextShortLE() throws IOException {
        return (short) this.nextUShortLE();
    }

    /**
     * Gets the next unsigned big-endian short from this source.
     *
     * @return the next unsigned big-endian short
     * @see #next()
     */
    default int nextUShort() throws IOException {
        return (this.next() << 8)
                | this.next();
    }

    /**
     * Gets the next unsigned little-endian short from this source.
     *
     * @return the next unsigned little-endian short
     * @see #next()
     */
    default int nextUShortLE() throws IOException {
        return this.next()
                | (this.next() << 8);
    }

    /**
     * Gets the next signed big-endian int from this source.
     *
     * @return the next signed big-endian int
     * @see #next()
     */
    default int nextInt() throws IOException {
        return (this.next() << 24)
                | (this.next() << 16)
                | (this.next() << 8)
                | this.next();
    }

    /**
     * Gets the next signed little-endian int from this source.
     *
     * @return the next signed little-endian int
     * @see #next()
     */
    default int nextIntLE() throws IOException {
        return this.next()
                | (this.next() << 8)
                | (this.next() << 16)
                | (this.next() << 24);
    }

    /**
     * Gets the next unsigned big-endian int from this source.
     *
     * @return the next unsigned big-endian int
     * @see #next()
     */
    default long nextUInt() throws IOException {
        return this.nextInt() & 0xFFFFFFFFL;
    }

    /**
     * Gets the next unsigned little-endian int from this source.
     *
     * @return the next unsigned little-endian int
     * @see #next()
     */
    default long nextUIntLE() throws IOException {
        return this.nextIntLE() & 0xFFFFFFFFL;
    }

    /**
     * Gets the next signed big-endian long from this source.
     *
     * @return the next signed big-endian long
     * @see #next()
     */
    default long nextLong() throws IOException {
        return ((long) this.next() << 56L)
                | ((long) this.next() << 48L)
                | ((long) this.next() << 40L)
                | ((long) this.next() << 32L)
                | ((long) this.next() << 24L)
                | ((long) this.next() << 16L)
                | ((long) this.next() << 8L)
                | (long) this.next();
    }

    /**
     * Gets the next signed little-endian long from this source.
     *
     * @return the next signed little-endian long
     * @see #next()
     */
    default long nextLongLE() throws IOException {
        return (long) this.next()
                | ((long) this.next() << 8L)
                | ((long) this.next() << 16L)
                | ((long) this.next() << 24L)
                | ((long) this.next() << 32L)
                | ((long) this.next() << 40L)
                | ((long) this.next() << 48L)
                | ((long) this.next() << 56L);
    }

    /**
     * Gets the next big-endian single-precision float from this source
     *
     * @return the next big-endian single-precision float
     * @see #next()
     */
    default float nextFloat() throws IOException {
        return Float.intBitsToFloat(this.nextInt());
    }

    /**
     * Gets the next little-endian single-precision float from this source
     *
     * @return the next little-endian single-precision float
     * @see #next()
     */
    default float nextFloatLE() throws IOException {
        return Float.intBitsToFloat(this.nextIntLE());
    }

    /**
     * Gets the next big-endian double-precision float from this source
     *
     * @return the next big-endian double-precision float
     * @see #next()
     */
    default double nextDouble() throws IOException {
        return Double.longBitsToDouble(this.nextLong());
    }

    /**
     * Gets the next little-endian double-precision float from this source
     *
     * @return the next little-endian double-precision float
     * @see #next()
     */
    default double nextDoubleLE() throws IOException {
        return Double.longBitsToDouble(this.nextLongLE());
    }

    /**
     * Gets the next Mojang-style varint from this source.
     * <p>
     * As described at https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong
     *
     * @return the next Mojang-style varint
     * @see #next()
     */
    default int nextVarint() throws IOException {
        int numRead = 0;
        int result = 0;
        int read;
        do {
            read = this.next();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);
        return result;
    }

    /**
     * Gets the next Mojang-style varlong from this source.
     * <p>
     * As described at https://wiki.vg/index.php?title=Protocol&oldid=14204#VarInt_and_VarLong
     *
     * @return the next Mojang-style varlong
     * @see #next()
     */
    default long nextVarlong() throws IOException {
        int numRead = 0;
        long result = 0;
        int read;
        do {
            read = this.next();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 10) {
                throw new RuntimeException("VarLong is too big");
            }
        } while ((read & 0b10000000) != 0);
        return result;
    }

    /**
     * Gets the next varint length-prefixed byte array from this source.
     *
     * @return the next varint length-prefixed byte array
     * @see #next()
     */
    default byte[] nextByteArray() throws IOException {
        byte[] arr = new byte[this.nextVarint()];
        this.nextBytes(arr);
        return arr;
    }

    /**
     * Gets the next varint length-prefixed UTF-8-encoded string from this source.
     *
     * @return the next varint length-prefixed UTF-8-encoded string
     * @see #next()
     */
    default String nextString() throws IOException {
        return this.nextString(StandardCharsets.UTF_8);
    }

    /**
     * Gets the next varint length-prefixed string from this source.
     *
     * @param charset the charset that the string is encoded in
     * @return the next varint length-prefixed string
     * @see #next()
     */
    //TODO: i think this can be optimized quite significantly
    default String nextString(@NonNull Charset charset) throws IOException {
        return new String(this.nextByteArray(), charset);
    }

    /**
     * Gets the next enum value from this source using a varint length-prefixed UTF-8 encoded string.
     *
     * @param clazz the enum class
     * @param <E>   the enum type
     * @return the next enum value
     * @see #next()
     */
    default <E extends Enum<E>> E nextEnum(@NonNull Class<E> clazz) throws IOException {
        return Enum.valueOf(clazz, this.nextString());
    }
}
