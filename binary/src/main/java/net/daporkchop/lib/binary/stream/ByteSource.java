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

package net.daporkchop.lib.binary.stream;

import lombok.NonNull;
import net.daporkchop.lib.common.util.PorkUtil;

/**
 * A source from which one may read single bytes for an indefinite length.
 *
 * @author DaPorkchop_
 */
public interface ByteSource {
    /**
     * Gets the next byte from this source.
     * <p>
     * If no further bytes are available, this will throw an unchecked exception.
     *
     * @return the next byte (unsigned)
     */
    int next();

    /**
     * Fills the given byte array with data from this source.
     * <p>
     * If no further bytes are available, this will throw an unchecked exception.
     *
     * @param arr the byte array to fill
     */
    default void next(@NonNull byte[] arr) {
        this.next(arr, 0, arr.length);
    }

    /**
     * Fills the given byte array with data from this source.
     * <p>
     * If no further bytes are available, this will throw an unchecked exception.
     *
     * @param arr the byte array to fill
     */
    default void next(@NonNull byte[] arr, int start, int count) {
        PorkUtil.assertValidArrayIndex(arr.length, start, count);

        count += start;
        for (int i = start; i < count; i++) {
            arr[i] = (byte) this.next();
        }
    }
}
