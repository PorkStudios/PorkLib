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

package net.daporkchop.lib.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

/**
 * An abstract representation of an HTTP status code.
 *
 * @author DaPorkchop_
 */
public interface StatusCode {
    /**
     * @return this status code's name (textual representation)
     */
    CharSequence name();

    /**
     * @return the status code's numeric ID
     */
    int code();

    /**
     * Gets the encoded value of this status.
     * <p>
     * The returned value will contain ASCII-encoded text formatted as such (without quotes):
     * " <numeric code> <text name>"
     * <p>
     * Intended only for internal use in order to obtain maximal performance when encoding responses.
     * <p>
     * Modifying the contents of the returned buffer will result in undefined behavior.
     *
     * @return the encoded value of this status
     */
    default ByteBuf encodedValue() {
        return Unpooled.wrappedBuffer(String.format(" %d %s", this.code(), this.name()).getBytes(StandardCharsets.US_ASCII));
    }
}
