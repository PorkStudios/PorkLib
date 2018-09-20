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

package net.daporkchop.lib.db.object.key.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.daporkchop.lib.db.object.key.KeyHasher;

import java.nio.charset.Charset;

/**
 * Hashes a string using a
 *
 * @author DaPorkchop_
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StringKeyHasher extends KeyHasher<String> {
    private static final Charset utf8 = Charset.forName("UTF-8");

    @NonNull
    private final HashKeyHasher hasher;

    @Override
    public boolean canGetKeyFromHash() {
        return false;
    }

    @Override
    public int getKeyLength() {
        return this.hasher.getKeyLength();
    }

    @Override
    public void hash(String key, byte[] hash) {
        byte[] bytes = key.getBytes(utf8);
        this.hasher.hash(bytes, hash);
    }
}
