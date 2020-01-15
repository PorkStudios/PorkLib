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

package net.daporkchop.lib.http.entity;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.entity.content.type.ContentType;
import net.daporkchop.lib.http.entity.transfer.ByteBufTransferSession;
import net.daporkchop.lib.http.entity.transfer.TransferSession;

/**
 * A simple implementation of {@link HttpEntity} that stores data in a {@link ByteBuf}.
 * <p>
 * Unlike {@link ByteArrayHttpEntity}, a single instance of this may safely be reused as the body of multiple requests/responses. However, the {@link ByteBuf}
 * must be manually released once the instance is no longer to be used.
 *
 * @author DaPorkchop_
 * @see ByteBufHttpEntity
 */
@Getter
@Accessors(fluent = true)
public final class ReusableByteBufHttpEntity extends ByteBufTransferSession implements HttpEntity {
    protected final ContentType type;

    public ReusableByteBufHttpEntity(@NonNull ContentType type, @NonNull ByteBuf buf) {
        super(buf);

        this.type = type;
    }

    @Override
    public TransferSession newSession() throws Exception {
        this.buf.retain();
        return this;
    }
}
