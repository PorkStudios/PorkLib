/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.crypto.key.serializer;

import lombok.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractKeySerializer<K> {
    public byte[] serialize(@NonNull K key) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            doSerialize(key, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public void serialize(@NonNull K key, @NonNull OutputStream stream) throws IOException {
        this.doSerialize(key, stream);
    }

    @SuppressWarnings("unchecked")
    public <T extends K> T deserialize(@NonNull byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        try {
            return (T) doDeserialize(bais);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends K> T deserialize(@NonNull InputStream stream) throws IOException {
        return (T) this.doDeserialize(stream);
    }

    protected abstract void doSerialize(K key, OutputStream baos) throws IOException;

    protected abstract K doDeserialize(InputStream bais) throws IOException;
}
