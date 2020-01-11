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

package net.daporkchop.lib.http.entity.content.type;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Represents a {@link ContentType} which is not known to PorkLib http.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Accessors(fluent = true)
public final class UnknownContentType implements ContentType {
    @NonNull
    protected final String mimeType;
    protected final String charsetName;

    public UnknownContentType(@NonNull String mimeType) {
        this(mimeType, null);
    }

    @Override
    public Charset charset() {
        if (this.charsetName != null) {
            try {
                return Charset.forName(this.charsetName);
            } catch (UnsupportedCharsetException e) {
                //swallow
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.formatted();
    }
}
