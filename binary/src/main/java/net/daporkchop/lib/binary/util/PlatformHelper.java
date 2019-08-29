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

package net.daporkchop.lib.binary.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.ByteOrder;

/**
 * Helps with some platform-specific binary things (such as reading/writing bytes using a different endianess).
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PlatformHelper {
    public final ByteOrder NATIVE_ORDER = ByteOrder.nativeOrder();
    public final boolean BIG_ENDIAN = NATIVE_ORDER == ByteOrder.BIG_ENDIAN;

    public short getShortBE(long addr)  {
        return BIG_ENDIAN ? PUnsafe.getShort(addr) : Short.reverseBytes(PUnsafe.getShort(addr));
    }

    public short getShortLE(long addr)  {
        return BIG_ENDIAN ? Short.reverseBytes(PUnsafe.getShort(addr)) : PUnsafe.getShort(addr);
    }

    public short getShortBE(Object ref, long addr)  {
        return BIG_ENDIAN ? PUnsafe.getShort(ref, addr) : Short.reverseBytes(PUnsafe.getShort(ref, addr));
    }

    public short getShortLE(Object ref, long addr)  {
        return BIG_ENDIAN ? Short.reverseBytes(PUnsafe.getShort(ref, addr)) : PUnsafe.getShort(ref, addr);
    }

    public char getCharBE(long addr)  {
        return BIG_ENDIAN ? PUnsafe.getChar(addr) : Character.reverseBytes(PUnsafe.getChar(addr));
    }

    public char getCharLE(long addr)  {
        return BIG_ENDIAN ? Character.reverseBytes(PUnsafe.getChar(addr)) : PUnsafe.getChar(addr);
    }

    public char getCharBE(Object ref, long addr)  {
        return BIG_ENDIAN ? PUnsafe.getChar(ref, addr) : Character.reverseBytes(PUnsafe.getChar(ref, addr));
    }

    public char getCharLE(Object ref, long addr)  {
        return BIG_ENDIAN ? Character.reverseBytes(PUnsafe.getChar(ref, addr)) : PUnsafe.getChar(ref, addr);
    }

    public int getIntBE(long addr)  {
        return BIG_ENDIAN ? PUnsafe.getInt(addr) : Integer.reverseBytes(PUnsafe.getInt(addr));
    }

    public int getIntLE(long addr)  {
        return BIG_ENDIAN ? Integer.reverseBytes(PUnsafe.getInt(addr)) : PUnsafe.getInt(addr);
    }

    public int getIntBE(Object ref, long addr)  {
        return BIG_ENDIAN ? PUnsafe.getInt(ref, addr) : Integer.reverseBytes(PUnsafe.getInt(ref, addr));
    }

    public int getIntLE(Object ref, long addr)  {
        return BIG_ENDIAN ? Integer.reverseBytes(PUnsafe.getInt(ref, addr)) : PUnsafe.getInt(ref, addr);
    }

    public long getLongBE(long addr)  {
        return BIG_ENDIAN ? PUnsafe.getLong(addr) : Long.reverseBytes(PUnsafe.getLong(addr));
    }

    public long getLongLE(long addr)  {
        return BIG_ENDIAN ? Long.reverseBytes(PUnsafe.getLong(addr)) : PUnsafe.getLong(addr);
    }

    public long getLongBE(Object ref, long addr)  {
        return BIG_ENDIAN ? PUnsafe.getLong(ref, addr) : Long.reverseBytes(PUnsafe.getLong(ref, addr));
    }

    public long getLongLE(Object ref, long addr)  {
        return BIG_ENDIAN ? Long.reverseBytes(PUnsafe.getLong(ref, addr)) : PUnsafe.getLong(ref, addr);
    }

    public void setShortBE(long addr, short val)    {
        PUnsafe.putShort(addr, BIG_ENDIAN ? val : Short.reverseBytes(val));
    }

    public void setShortLE(long addr, short val)    {
        PUnsafe.putShort(addr, BIG_ENDIAN ? Short.reverseBytes(val) : val);
    }

    public void setShortBE(Object ref, long addr, short val)    {
        PUnsafe.putShort(ref, addr, BIG_ENDIAN ? val : Short.reverseBytes(val));
    }

    public void setShortLE(Object ref, long addr, short val)    {
        PUnsafe.putShort(ref, addr, BIG_ENDIAN ? Short.reverseBytes(val) : val);
    }

    public void setCharBE(long addr, char val)    {
        PUnsafe.putChar(addr, BIG_ENDIAN ? val : Character.reverseBytes(val));
    }

    public void setCharLE(long addr, char val)    {
        PUnsafe.putChar(addr, BIG_ENDIAN ? Character.reverseBytes(val) : val);
    }

    public void setCharBE(Object ref, long addr, char val)    {
        PUnsafe.putChar(ref, addr, BIG_ENDIAN ? val : Character.reverseBytes(val));
    }

    public void setCharLE(Object ref, long addr, char val)    {
        PUnsafe.putChar(ref, addr, BIG_ENDIAN ? Character.reverseBytes(val) : val);
    }

    public void setIntBE(long addr, int val)    {
        PUnsafe.putInt(addr, BIG_ENDIAN ? val : Integer.reverseBytes(val));
    }

    public void setIntLE(long addr, int val)    {
        PUnsafe.putInt(addr, BIG_ENDIAN ? Integer.reverseBytes(val) : val);
    }

    public void setIntBE(Object ref, long addr, int val)    {
        PUnsafe.putInt(ref, addr, BIG_ENDIAN ? val : Integer.reverseBytes(val));
    }

    public void setIntLE(Object ref, long addr, int val)    {
        PUnsafe.putInt(ref, addr, BIG_ENDIAN ? Integer.reverseBytes(val) : val);
    }

    public void setLongBE(long addr, long val)    {
        PUnsafe.putLong(addr, BIG_ENDIAN ? val : Long.reverseBytes(val));
    }

    public void setLongLE(long addr, long val)    {
        PUnsafe.putLong(addr, BIG_ENDIAN ? Long.reverseBytes(val) : val);
    }

    public void setLongBE(Object ref, long addr, long val)    {
        PUnsafe.putLong(ref, addr, BIG_ENDIAN ? val : Long.reverseBytes(val));
    }

    public void setLongLE(Object ref, long addr, long val)    {
        PUnsafe.putLong(ref, addr, BIG_ENDIAN ? Long.reverseBytes(val) : val);
    }
}
