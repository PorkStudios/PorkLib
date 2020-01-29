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

package net.daporkchop.lib.binary.oio.appendable;

import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.chars.SingleCharSequence;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.common.util.PorkUtil;

import java.nio.charset.StandardCharsets;

/**
 * Implementation of {@link PAppendable} that appends ASCII-encoded text to a {@link ByteBuf}.
 * <p>
 * This implementation is not thread-safe.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class ASCIIByteBufAppendable implements PAppendable {
    @NonNull
    protected final ByteBuf   buf;
    @NonNull
    protected final String    lineEnding;
    @Getter(AccessLevel.NONE)
    protected final SingleCharSequence singleCharSequence = new SingleCharSequence();

    public ASCIIByteBufAppendable(@NonNull ByteBuf buf) {
        this(buf, PlatformInfo.OPERATING_SYSTEM.lineEnding());
    }

    @Override
    public ASCIIByteBufAppendable append(CharSequence seq) {
        this.buf.writeCharSequence(seq == null ? "null" : seq, StandardCharsets.US_ASCII);
        return this;
    }

    @Override
    public ASCIIByteBufAppendable append(CharSequence seq, int start, int end) {
        this.buf.writeCharSequence(PorkUtil.subSequence(seq == null ? "null" : seq, start, end), StandardCharsets.US_ASCII);
        return this;
    }

    @Override
    public ASCIIByteBufAppendable append(char c) {
        this.buf.writeByte(AsciiString.c2b(c));
        return this;
    }

    @Override
    public ASCIIByteBufAppendable appendLn() {
        return this.append(this.lineEnding);
    }

    @Override
    public void close() {
        //no-op
    }
}
