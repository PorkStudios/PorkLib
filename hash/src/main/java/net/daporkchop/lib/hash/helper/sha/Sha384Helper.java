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

package net.daporkchop.lib.hash.helper.sha;

import net.daporkchop.lib.hash.impl.skid.sha.Sha384Impl;

/**
 * A thread-safe wrapper for the Sha384 hashing algorithm
 *
 * @author DaPorkchop_
 */
public class Sha384Helper {
    private static final Sha384Helper INSTANCE = new Sha384Helper();
    private final ThreadLocal<Sha384Impl> tl = ThreadLocal.withInitial(Sha384Impl::new);

    private Sha384Helper() {
    }

    public static byte[] sha384(byte[] toHash) {
        return INSTANCE.hash(toHash);
    }

    public static byte[] sha384(byte[]... toHash) {
        return INSTANCE.hash(toHash);
    }

    private byte[] hash(byte[] toHash) {
        Sha384Impl i = tl.get();
        i.update(toHash, 0, toHash.length);
        byte[] hash = i.digest();
        i.reset();
        return hash;
    }

    private byte[] hash(byte[]... toHash) {
        Sha384Impl i = tl.get();
        for (byte[] b : toHash)
            i.update(b, 0, b.length);
        byte[] hash = i.digest();
        i.reset();
        return hash;
    }
}
