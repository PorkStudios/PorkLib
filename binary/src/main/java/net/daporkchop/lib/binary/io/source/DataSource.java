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

import java.io.IOException;

/**
 * Extension on top of {@link ByteSource} which allows for reading higher-level data types (such as primitives or
 * UTF-8 text).
 *
 * @author DaPorkchop_
 */
public interface DataSource extends ByteSource {
    /**
     * Gets the next signed big-endian short from this source.
     *
     * @return the next signed big-endian short
     */
    default short nextShort() throws IOException {
        return (short) this.nextUShort();
    }

    /**
     * Gets the next signed little-endian short from this source.
     *
     * @return the next signed little-endian short
     */
    default short nextShortLE() throws IOException {
        return (short) this.nextUShortLE();
    }

    /**
     * Gets the next unsigned big-endian short from this source.
     *
     * @return the next unsigned big-endian short
     */
    default int nextUShort() throws IOException {
        return (this.next() << 8)
                | this.next();
    }

    /**
     * Gets the next unsigned little-endian short from this source.
     *
     * @return the next unsigned little-endian short
     */
    default int nextUShortLE() throws IOException {
        return this.next()
                | (this.next() << 8);
    }

    /**
     * Gets the next signed big-endian int from this source.
     *
     * @return the next signed big-endian int
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
     */
    default long nextUInt() throws IOException {
        return this.nextInt() & 0xFFFFFFFFL;
    }

    /**
     * Gets the next unsigned little-endian int from this source.
     *
     * @return the next unsigned little-endian int
     */
    default long nextUIntLE() throws IOException {
        return this.nextIntLE() & 0xFFFFFFFFL;
    }

    /**
     * Gets the next signed big-endian long from this source.
     *
     * @return the next signed big-endian long
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
}
