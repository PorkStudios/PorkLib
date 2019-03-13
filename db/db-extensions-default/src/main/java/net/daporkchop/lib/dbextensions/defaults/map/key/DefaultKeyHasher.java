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

package net.daporkchop.lib.dbextensions.defaults.map.key;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.optimizations.FastByteArrayOutputStream;
import net.daporkchop.lib.db.container.map.KeyHasher;
import net.daporkchop.lib.hash.util.Digest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Hashes a {@link Serializable} to testMethodThing constant-length hash.
 * <p>
 * Cannot get key from hashed value.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class DefaultKeyHasher<K extends Serializable> implements KeyHasher<K> {
    public static final DefaultKeyHasher<Serializable> DEFAULT = new DefaultKeyHasher<>(Digest.MD5);

    @NonNull
    private final Digest digest;

    @Override
    public byte[] hash(@NonNull K key) {
        ByteArrayOutputStream baos = new FastByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this.digest.hash(baos.toByteArray()).getHash();
    }

    @Override
    public int getHashLength() {
        return this.digest.getHashSize();
    }
}
