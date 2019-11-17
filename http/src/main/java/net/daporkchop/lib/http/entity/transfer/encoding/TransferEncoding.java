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

package net.daporkchop.lib.http.entity.transfer.encoding;

import lombok.NonNull;

/**
 * A value for the "Transfer-Encoding" HTTP header.
 *
 * @author DaPorkchop_
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Transfer-Encoding">Transfer-Encoding at Mozilla</a>
 */
//TODO: i think this could probably do with a bit more things?
public interface TransferEncoding {
    /**
     * Gets a {@link TransferEncoding} which matches the given name.
     *
     * @param name the name of the {@link TransferEncoding}
     * @return a {@link TransferEncoding} which matches the given name
     */
    static TransferEncoding of(@NonNull String name) {
        try {
            return StandardTransferEncoding.valueOf(name);
        } catch (IllegalArgumentException e) {
            return new UnknownTransferEncoding(name);
        }
    }

    /**
     * @return the textual name of this {@link TransferEncoding} method
     */
    String name();
}
