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

package net.daporkchop.lib.binary.chars;

import io.netty.util.internal.PlatformDependent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.capability.Releasable;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;
import sun.nio.ch.DirectBuffer;

import java.nio.MappedByteBuffer;

/**
 * A wrapper around a direct memory address to allow it to be used as a {@link CharSequence} of 1-byte characters (aka. Latin, Extended ASCII or
 * ISO/IEC 8859-1).
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class DirectASCIISequence implements CharSequence {
    protected final long addr;
    protected final int  length;

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= this.length) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        return (char) (PUnsafe.getByte(this.addr + index) & 0xFF);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        PorkUtil.assertInRange(this.length, start, end);
        return start == 0 && end == this.length ? this : this.slice(start, end - start);
    }

    protected CharSequence slice(int start, int len)    {
        return new DirectASCIISequence(this.addr + start, len);
    }

    @Override
    public int hashCode() {
        int i = 0;
        for (long addr = this.addr, end = addr + this.length; addr != end; addr++) {
            i = i * 31 + (PUnsafe.getByte(addr) & 0xFF);
        }
        return i;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof CharSequence) {
            CharSequence seq = (CharSequence) obj;
            int len = this.length;
            if (seq.length() != len) {
                return false;
            }
            long addr = this.addr;
            int i = 0;
            while (i < len && (char) (PUnsafe.getByte(addr + i) & 0xFF) == seq.charAt(i)) {
                i++;
            }
            return i == len;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        final long addr = this.addr;
        final int len = this.length;
        char[] arr = new char[len];
        for (int i = 0; i < len; i++) {
            arr[i] = (char) (PUnsafe.getByte(addr + i) & 0xFF);
        }
        return PorkUtil.wrap(arr);
    }

    /**
     * A {@link DirectASCIISequence} wrapping a {@link MappedByteBuffer}.
     *
     * @author DaPorkchop_
     */
    @Getter
    @Accessors(fluent = true)
    public static final class Mapped extends DirectASCIISequence implements Releasable {
        private MappedByteBuffer buffer;

        public Mapped(@NonNull MappedByteBuffer buffer) {
            this(buffer, PlatformDependent.directBufferAddress(buffer) + buffer.position(), buffer.remaining(), false);
        }

        public Mapped(@NonNull MappedByteBuffer buffer, boolean load) {
            this(buffer, PlatformDependent.directBufferAddress(buffer) + buffer.position(), buffer.remaining(), load);
        }

        public Mapped(@NonNull MappedByteBuffer buffer, long address, int size) {
            this(buffer, address, size, false);
        }

        public Mapped(@NonNull MappedByteBuffer buffer, long address, int size, boolean load) {
            super(address, size);

            this.buffer = buffer;

            if (load)   {
                buffer.load();
            }
        }

        @Override
        protected CharSequence slice(int start, int len) {
            return new Mapped(this.buffer, this.addr + start, len);
        }

        @Override
        public synchronized void release() throws AlreadyReleasedException {
            if (this.buffer != null)    {
                PorkUtil.release(this.buffer);
                this.buffer = null;
            } else {
                throw new AlreadyReleasedException();
            }
        }
    }
}
