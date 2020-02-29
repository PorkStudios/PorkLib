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

package net.daporkchop.lib.common.misc;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * Stores two values.
 * <p>
 * Really needs to get cleaned up...
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public final class Tuple<A, B> {
    protected static final long A_OFFSET = PUnsafe.pork_getOffset(Tuple.class, "a");
    protected static final long B_OFFSET = PUnsafe.pork_getOffset(Tuple.class, "b");

    protected A a;
    protected B b;

    public boolean isANull() {
        return this.a == null;
    }

    public boolean isANonNull() {
        return this.a != null;
    }

    public boolean isBNull() {
        return this.b == null;
    }

    public boolean isBNonNull() {
        return this.b != null;
    }

    public Tuple<A, B> atomicSetA(A a) {
        PUnsafe.putObjectVolatile(this, A_OFFSET, a);
        return this;
    }

    public Tuple<A, B> atomicSetB(B b) {
        PUnsafe.putObjectVolatile(this, B_OFFSET, b);
        return this;
    }

    public A atomicSwapA(A a) {
        return PUnsafe.pork_swapObject(this, A_OFFSET, a);
    }

    public B atomicSwapB(B b) {
        return PUnsafe.pork_swapObject(this, B_OFFSET, b);
    }
}
