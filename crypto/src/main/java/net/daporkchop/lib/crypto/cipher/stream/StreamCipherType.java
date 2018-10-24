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

package net.daporkchop.lib.crypto.cipher.stream;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.engines.Grain128Engine;
import org.bouncycastle.crypto.engines.Salsa20Engine;
import org.bouncycastle.crypto.engines.XSalsa20Engine;

import java.util.function.Supplier;

public enum StreamCipherType {
    CHACHA10_128("ChaCha10 (128-bit)", () -> new ChaChaEngine(10), 16, 8),
    CHACHA20_128("ChaCha20 (128-bit)", ChaChaEngine::new, 16, 8),
    CHACHA40_128("ChaCha40 (128-bit)", () -> new ChaChaEngine(40), 16, 8),
    CHACHA10_256("ChaCha10 (256-bit)", () -> new ChaChaEngine(10), 32, 8),
    CHACHA20_256("ChaCha20 (256-bit)", ChaChaEngine::new, 32, 8),
    CHACHA40_256("ChaCha40 (256-bit)", () -> new ChaChaEngine(40), 32, 8),
    //This guy is totally broken: GRAIN128("Grain-128", Grain128Engine::new, 12, 12),
    SALSA10("Salsa10", () -> new Salsa20Engine(10), 16, 8),
    SALSA20("Salsa20", Salsa20Engine::new, 16, 8),
    XSALSA20("XSalsa20", XSalsa20Engine::new, 32, 24),
    BLOCK_CIPHER("Pseudo-Stream (Block)", () -> {
        throw new UnsupportedOperationException();
    }, -1);

    public final String name;
    private final Supplier<StreamCipher> cipherSupplier;
    public final int keySize;
    public final int ivSize;

    StreamCipherType(@NonNull String name, @NonNull Supplier<StreamCipher> cipherSupplier, int keySize) {
        this(name, cipherSupplier, keySize, keySize);
    }

    StreamCipherType(@NonNull String name, @NonNull Supplier<StreamCipher> cipherSupplier, int keySize, int ivSize) {
        this.name = name;
        this.cipherSupplier = cipherSupplier;
        this.keySize = keySize;
        this.ivSize = ivSize;
    }

    public StreamCipher create()   {
        return this.cipherSupplier.get();
    }
}
