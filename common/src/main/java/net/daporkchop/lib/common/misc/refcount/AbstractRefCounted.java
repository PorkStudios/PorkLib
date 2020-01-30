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

package net.daporkchop.lib.common.misc.refcount;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * An abstract implementation of {@link RefCounted}, which handles incrementing and decrementing the counter internally
 * and provides a single method to be implemented by subclasses which does the actual releasing.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractRefCounted implements RefCounted {
    protected static final long REFCNT_OFFSET = PUnsafe.pork_getOffset(AbstractRefCounted.class, "refCnt");

    private volatile int refCnt = 1;

    @Override
    public RefCounted retain() throws AlreadyReleasedException {
        int refCnt;
        do {
            if ((refCnt = PUnsafe.getIntVolatile(this, REFCNT_OFFSET)) == 0) {
                throw new AlreadyReleasedException();
            }
        } while (!PUnsafe.compareAndSwapInt(this, REFCNT_OFFSET, refCnt, refCnt + 1));
        return this;
    }

    @Override
    public boolean release() throws AlreadyReleasedException {
        int refCnt;
        do {
            if ((refCnt = PUnsafe.getIntVolatile(this, REFCNT_OFFSET)) == 0) {
                throw new AlreadyReleasedException();
            }
        } while (!PUnsafe.compareAndSwapInt(this, REFCNT_OFFSET, refCnt, refCnt - 1));

        if (refCnt == 1) {
            //old reference count was 1, meaning now it's 0 and the instance should be released
            this.doRelease();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Actually releases this instance.
     * <p>
     * This method will only be called once once the reference count reaches 0.
     */
    protected abstract void doRelease();
}
