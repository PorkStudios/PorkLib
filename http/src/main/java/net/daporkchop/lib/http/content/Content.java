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

package net.daporkchop.lib.http.content;

/**
 * Represents some form of content that will be sent over an HTTP connection.
 *
 * @author DaPorkchop_
 */
//TODO: some way of actually writing the data needs to be added
public interface Content {
    /**
     * @return the content's MIME type
     */
    String type();

    /**
     * Gets the {@link HttpCompression} type used for encoding this content's data.
     * <p>
     * This option is only intended for use by implementations that will do the compression themselves and write it directly to the destination output, as
     * it will not be done automatically by the internal HTTP engine.
     *
     * @return the content's compression type
     */
    default HttpCompression compression() {
        return HttpCompression.IDENTITY;
    }

    /**
     * Gets the length of the data (in bytes).
     * <p>
     * If {@link #compression()} is set, this method should return the compressed size of the data.
     * <p>
     * If the length of the data isn't known for any reason, this method should return {@code -1L}.
     *
     * @return the length of the data (in bytes), or {@code -1L} if it isn't known
     */
    long size();
}
