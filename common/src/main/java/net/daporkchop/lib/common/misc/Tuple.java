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
