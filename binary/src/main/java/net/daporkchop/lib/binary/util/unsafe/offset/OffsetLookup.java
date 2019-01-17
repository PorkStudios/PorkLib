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

package net.daporkchop.lib.binary.util.unsafe.offset;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.netty.NettyUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.IdentityHashMap;
import java.util.Map;

import static net.daporkchop.lib.common.util.PUnsafe.*;
import static net.daporkchop.lib.common.util.PorkUtil.classForName;

/**
 * A simple mapping of classes to their offsetters
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor
@Getter
@SuppressWarnings("unchecked")
public class OffsetLookup {
    public static final OffsetLookup INSTANCE = new OffsetLookup().addDefault();

    @NonNull
    protected final Map<Class<?>, Offsetter> offsetters = new IdentityHashMap<>(); //TODO again, we really need to make us a map optimized for using classes as keys

    /**
     * Registers the default mappings
     *
     * @return this lookup
     */
    public OffsetLookup addDefault() {
        if (NettyUtil.NETTY_PRESENT)    {
            //TODO: add offsetters for all netty buffer implementations
        }

        long byteBuffer_hb_offset = pork_getOffset(ByteBuffer.class, "hb");
        long buffer_address_offset = pork_getOffset(Buffer.class, "address");

        return this
                //arrays
                .add(boolean[].class, Offsetter.of(v -> ARRAY_BOOLEAN_BASE_OFFSET, v -> (long) v.length * ARRAY_BOOLEAN_INDEX_SCALE))
                .add(byte[].class, Offsetter.of(v -> ARRAY_BYTE_BASE_OFFSET, v -> (long) v.length * ARRAY_BYTE_INDEX_SCALE))
                .add(short[].class, Offsetter.of(v -> ARRAY_SHORT_BASE_OFFSET, v -> (long) v.length * ARRAY_SHORT_INDEX_SCALE))
                .add(int[].class, Offsetter.of(v -> ARRAY_INT_BASE_OFFSET, v -> (long) v.length * ARRAY_INT_INDEX_SCALE))
                .add(long[].class, Offsetter.of(v -> ARRAY_LONG_BASE_OFFSET, v -> (long) v.length * ARRAY_LONG_INDEX_SCALE))
                .add(float[].class, Offsetter.of(v -> ARRAY_FLOAT_BASE_OFFSET, v -> (long) v.length * ARRAY_FLOAT_INDEX_SCALE))
                .add(double[].class, Offsetter.of(v -> ARRAY_DOUBLE_BASE_OFFSET, v -> (long) v.length * ARRAY_DOUBLE_INDEX_SCALE))
                .add(char[].class, Offsetter.of(v -> ARRAY_CHAR_BASE_OFFSET, v -> (long) v.length * ARRAY_CHAR_INDEX_SCALE))
                .add(Object[].class, Offsetter.of(v -> ARRAY_OBJECT_BASE_OFFSET, v -> (long) v.length * ARRAY_OBJECT_INDEX_SCALE))
                //nio buffers
                .add(classForName("java.nio.HeapByteBuffer"), Offsetter.of(v -> ARRAY_BYTE_BASE_OFFSET, ByteBuffer::capacity, v -> getObject(v, byteBuffer_hb_offset)))
                .add(classForName("java.nio.HeapByteBufferR"), this.offsetters.get(classForName("java.nio.HeapByteBuffer")))
                .add(classForName("java.nio.DirectByteBuffer"), Offsetter.of(v -> getLong(v, buffer_address_offset), ByteBuffer::capacity))
                .add(classForName("java.nio.DirectByteBufferR"), this.offsetters.get(classForName("java.nio.DirectByteBuffer")));
    }

    /**
     * Adds a new offsetter to this lookup
     *
     * @param clazz     the class of the type to add
     * @param offsetter the offsetter to use
     * @param <T>       the type that will be looked up
     * @return this lookup
     */
    public <T> OffsetLookup add(@NonNull Class<T> clazz, @NonNull Offsetter<T> offsetter) {
        this.offsetters.put(clazz, offsetter);
        return this;
    }

    /**
     * Gets the offset data for a given object
     *
     * @param o the object
     * @return the offset data for that object
     */
    public OffsetData getData(@NonNull Object o) {
        Class<?> clazz = o.getClass();
        Offsetter offsetter;
        if (Offsettable.class.isAssignableFrom(clazz)) {
            return ((Offsettable) o).data();
        } else if ((offsetter = this.offsetters.get(clazz)) != null) {
            return offsetter.data(o);
        } else {
            throw new IllegalArgumentException(String.format("Unregistered type: %s", clazz.getCanonicalName()));
        }
    }

    /**
     * Gets the offset for a given object
     *
     * @param o the object
     * @return the offset for that object
     */
    public long getOffset(@NonNull Object o) {
        Class<?> clazz = o.getClass();
        Offsetter offsetter;
        if (Offsettable.class.isAssignableFrom(clazz)) {
            return ((Offsettable) o).memoryOffset();
        } else if ((offsetter = this.offsetters.get(clazz)) != null) {
            return offsetter.memoryOffset(o);
        } else {
            throw new IllegalArgumentException(String.format("Unregistered type: %s", clazz.getCanonicalName()));
        }
    }

    /**
     * Gets the length for a given object
     *
     * @param o the object
     * @return the length for that object
     */
    public long getLength(@NonNull Object o) {
        Class<?> clazz = o.getClass();
        Offsetter offsetter;
        if (Offsettable.class.isAssignableFrom(clazz)) {
            return ((Offsettable) o).memoryLength();
        } else if ((offsetter = this.offsetters.get(clazz)) != null) {
            return offsetter.memoryLength(o);
        } else {
            throw new IllegalArgumentException(String.format("Unregistered type: %s", clazz.getCanonicalName()));
        }
    }
}
