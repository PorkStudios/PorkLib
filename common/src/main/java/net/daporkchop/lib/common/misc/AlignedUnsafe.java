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

package net.daporkchop.lib.common.misc;

import io.netty.util.internal.PlatformDependent;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.Endianess;
import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * {@link PUnsafe}, but with extra utilities for byte ordering and alignment-safety.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class AlignedUnsafe {
    /**
     * Whether or not unaligned memory accesses are supported.
     */
    public final boolean UNALIGNED = PorkUtil.classExistsWithName("io.netty.util.internal.PlatformDependent")
            ? PlatformDependent.isUnaligned()
            : PUnsafe.pork_getStaticField(
            PorkUtil.classForName("java.nio.Bits", false, ClassLoader.getSystemClassLoader()),
            PlatformInfo.JAVA_VERSION >= 11 ? "UNALIGNED" : "unaligned"
    ).getBoolean();

    public short getShortBE(long address) {
        if (UNALIGNED || (address & 1L) == 0L) {
            short s = PUnsafe.getShort(address);
            return Endianess.BIG_ENDIAN_NATIVE ? s : Short.reverseBytes(s);
        } else {
            return (short) (PUnsafe.getByte(address) << 8
                    | PUnsafe.getByte(address + 1L) & 0xFF);
        }
    }

    public short getShortLE(long address) {
        if (UNALIGNED || (address & 1L) == 0L) {
            short s = PUnsafe.getShort(address);
            return Endianess.LITTLE_ENDIAN_NATIVE ? s : Short.reverseBytes(s);
        } else {
            return (short) (PUnsafe.getByte(address) & 0xFF
                    | PUnsafe.getByte(address + 1L) << 8);
        }
    }

    public short getShortN(long address) {
        return Endianess.BIG_ENDIAN_NATIVE ? getShortBE(address) : getShortLE(address);
    }

    public char getCharBE(long address) {
        if (UNALIGNED || (address & 1L) == 0L) {
            char s = PUnsafe.getChar(address);
            return Endianess.BIG_ENDIAN_NATIVE ? s : Character.reverseBytes(s);
        } else {
            return (char) ((PUnsafe.getByte(address) & 0xFF) << 8
                    | PUnsafe.getByte(address + 1L) & 0xFF);
        }
    }

    public char getCharLE(long address) {
        if (UNALIGNED || (address & 1L) == 0L) {
            char s = PUnsafe.getChar(address);
            return Endianess.LITTLE_ENDIAN_NATIVE ? s : Character.reverseBytes(s);
        } else {
            return (char) (PUnsafe.getByte(address) & 0xFF
                    | (PUnsafe.getByte(address + 1L) & 0xFF) << 8);
        }
    }

    public char getCharN(long address) {
        return Endianess.BIG_ENDIAN_NATIVE ? getCharBE(address) : getCharLE(address);
    }

    public int getIntBE(long address) {
        if (UNALIGNED || (address & 3L) == 0L) {
            int s = PUnsafe.getInt(address);
            return Endianess.BIG_ENDIAN_NATIVE ? s : Integer.reverseBytes(s);
        } else {
            return PUnsafe.getByte(address) << 24
                    | (PUnsafe.getByte(address + 1L) & 0xFF) << 16
                    | (PUnsafe.getByte(address + 2L) & 0xFF) << 8
                    | PUnsafe.getByte(address + 3L) & 0xFF;
        }
    }

    public int getIntLE(long address) {
        if (UNALIGNED || (address & 3L) == 0L) {
            int s = PUnsafe.getInt(address);
            return Endianess.LITTLE_ENDIAN_NATIVE ? s : Integer.reverseBytes(s);
        } else {
            return PUnsafe.getByte(address) & 0xFF
                    | (PUnsafe.getByte(address + 1L) & 0xFF) << 8
                    | (PUnsafe.getByte(address + 2L) & 0xFF) << 16
                    | PUnsafe.getByte(address + 3L) << 24;
        }
    }

    public int getIntN(long address) {
        return Endianess.BIG_ENDIAN_NATIVE ? getIntBE(address) : getIntLE(address);
    }

    public long getLongBE(long address) {
        if (UNALIGNED || (address & 7L) == 0L) {
            long s = PUnsafe.getLong(address);
            return Endianess.BIG_ENDIAN_NATIVE ? s : Long.reverseBytes(s);
        } else {
            return ((long) PUnsafe.getByte(address)) << 56L
                    | (PUnsafe.getByte(address + 1L) & 0xFFL) << 48L
                    | (PUnsafe.getByte(address + 2L) & 0xFFL) << 40L
                    | (PUnsafe.getByte(address + 3L) & 0xFFL) << 32L
                    | (PUnsafe.getByte(address + 4L) & 0xFFL) << 24L
                    | (PUnsafe.getByte(address + 5L) & 0xFFL) << 16L
                    | (PUnsafe.getByte(address + 6L) & 0xFFL) << 8L
                    | PUnsafe.getByte(address + 7L) & 0xFFL;
        }
    }

    public long getLongLE(long address) {
        if (UNALIGNED || (address & 7L) == 0L) {
            long s = PUnsafe.getLong(address);
            return Endianess.LITTLE_ENDIAN_NATIVE ? s : Long.reverseBytes(s);
        } else {
            return PUnsafe.getByte(address) & 0xFFL
                    | (PUnsafe.getByte(address + 1L) & 0xFFL) << 8L
                    | (PUnsafe.getByte(address + 2L) & 0xFFL) << 16L
                    | (PUnsafe.getByte(address + 3L) & 0xFFL) << 24L
                    | (PUnsafe.getByte(address + 4L) & 0xFFL) << 32L
                    | (PUnsafe.getByte(address + 5L) & 0xFFL) << 40L
                    | (PUnsafe.getByte(address + 6L) & 0xFFL) << 48L
                    | ((long) PUnsafe.getByte(address + 7L)) << 56L;
        }
    }

    public long getLongN(long address) {
        return Endianess.BIG_ENDIAN_NATIVE ? getLongBE(address) : getLongLE(address);
    }

    public void putShortBE(long address, short val) {
        if (UNALIGNED || (address & 1L) == 0L) {
            PUnsafe.putShort(address, Endianess.BIG_ENDIAN_NATIVE ? val : Short.reverseBytes(val));
        } else {
            PUnsafe.putByte(address, (byte) (val >>> 8));
            PUnsafe.putByte(address + 1L, (byte) val);
        }
    }

    public void putShortLE(long address, short val) {
        if (UNALIGNED || (address & 1L) == 0L) {
            PUnsafe.putShort(address, Endianess.BIG_ENDIAN_NATIVE ? val : Short.reverseBytes(val));
        } else {
            PUnsafe.putByte(address, (byte) val);
            PUnsafe.putByte(address + 1L, (byte) (val >>> 8));
        }
    }

    public void putShortN(long address, short val) {
        if (Endianess.BIG_ENDIAN_NATIVE) {
            putShortBE(address, val);
        } else {
            putShortLE(address, val);
        }
    }

    public void putCharBE(long address, char val) {
        if (UNALIGNED || (address & 1L) == 0L) {
            PUnsafe.putChar(address, Endianess.BIG_ENDIAN_NATIVE ? val : Character.reverseBytes(val));
        } else {
            PUnsafe.putByte(address, (byte) (val >>> 8));
            PUnsafe.putByte(address + 1L, (byte) val);
        }
    }

    public void putCharLE(long address, char val) {
        if (UNALIGNED || (address & 1L) == 0L) {
            PUnsafe.putChar(address, Endianess.BIG_ENDIAN_NATIVE ? val : Character.reverseBytes(val));
        } else {
            PUnsafe.putByte(address, (byte) val);
            PUnsafe.putByte(address + 1L, (byte) (val >>> 8));
        }
    }

    public void putCharN(long address, char val) {
        if (Endianess.BIG_ENDIAN_NATIVE) {
            putCharBE(address, val);
        } else {
            putCharLE(address, val);
        }
    }

    public void putIntBE(long address, int val) {
        if (UNALIGNED || (address & 1L) == 0L) {
            PUnsafe.putInt(address, Endianess.BIG_ENDIAN_NATIVE ? val : Integer.reverseBytes(val));
        } else {
            PUnsafe.putByte(address, (byte) (val >>> 24));
            PUnsafe.putByte(address + 1L, (byte) (val >>> 16));
            PUnsafe.putByte(address + 2L, (byte) (val >>> 8));
            PUnsafe.putByte(address + 3L, (byte) val);
        }
    }

    public void putIntLE(long address, int val) {
        if (UNALIGNED || (address & 1L) == 0L) {
            PUnsafe.putInt(address, Endianess.BIG_ENDIAN_NATIVE ? val : Integer.reverseBytes(val));
        } else {
            PUnsafe.putByte(address, (byte) val);
            PUnsafe.putByte(address + 1L, (byte) (val >>> 8));
            PUnsafe.putByte(address + 2L, (byte) (val >>> 16));
            PUnsafe.putByte(address + 3L, (byte) (val >>> 24));
        }
    }

    public void putIntN(long address, int val) {
        if (Endianess.BIG_ENDIAN_NATIVE) {
            putIntBE(address, val);
        } else {
            putIntLE(address, val);
        }
    }

    public void putLongBE(long address, long val) {
        if (UNALIGNED || (address & 1L) == 0L) {
            PUnsafe.putLong(address, Endianess.BIG_ENDIAN_NATIVE ? val : Long.reverseBytes(val));
        } else {
            PUnsafe.putByte(address, (byte) (val >>> 56L));
            PUnsafe.putByte(address + 1L, (byte) (val >>> 48L));
            PUnsafe.putByte(address + 2L, (byte) (val >>> 40L));
            PUnsafe.putByte(address + 3L, (byte) (val >>> 32L));
            PUnsafe.putByte(address + 4L, (byte) (val >>> 24L));
            PUnsafe.putByte(address + 5L, (byte) (val >>> 16L));
            PUnsafe.putByte(address + 6L, (byte) (val >>> 8L));
            PUnsafe.putByte(address + 7L, (byte) val);
        }
    }

    public void putLongLE(long address, long val) {
        if (UNALIGNED || (address & 1L) == 0L) {
            PUnsafe.putLong(address, Endianess.BIG_ENDIAN_NATIVE ? val : Long.reverseBytes(val));
        } else {
            PUnsafe.putByte(address, (byte) val);
            PUnsafe.putByte(address + 1L, (byte) (val >>> 8L));
            PUnsafe.putByte(address + 2L, (byte) (val >>> 16L));
            PUnsafe.putByte(address + 3L, (byte) (val >>> 24L));
            PUnsafe.putByte(address + 4L, (byte) (val >>> 32L));
            PUnsafe.putByte(address + 5L, (byte) (val >>> 40L));
            PUnsafe.putByte(address + 6L, (byte) (val >>> 48L));
            PUnsafe.putByte(address + 7L, (byte) (val >>> 56L));
        }
    }

    public void putLongN(long address, long val) {
        if (Endianess.BIG_ENDIAN_NATIVE) {
            putLongBE(address, val);
        } else {
            putLongLE(address, val);
        }
    }
}
