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

package net.daporkchop.lib.crypto.keygen;

import java.security.SecureRandom;

/**
 * A wrapper for SecureRandom, to prevent having to create multiple instances
 *
 * @author DaPorkchop_
 */
public class KeyRandom {
    private static final SecureRandom random = new SecureRandom();

    public static byte[] getBytes(int length) {
        synchronized (random) {
            byte[] b = new byte[length];
            getBytes(b);
            return b;
        }
    }

    public static void getBytes(byte[] bytes) {
        synchronized (random) {
            random.nextBytes(bytes);
        }
    }

    public static byte[] getBytes(int length, byte[] seed) {
        synchronized (random) {
            byte[] b = new byte[length];
            getBytes(b, seed);
            return b;
        }
    }

    public static void getBytes(byte[] bytes, byte[] seed) {
        synchronized (random) {
            random.setSeed(seed);
            random.nextBytes(bytes);
        }
    }

    private KeyRandom() {
    }
}
