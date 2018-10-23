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

package net.daporkchop.lib.crypto.cipher;

import lombok.AllArgsConstructor;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.engines.Grain128Engine;
import org.bouncycastle.crypto.engines.Salsa20Engine;
import org.bouncycastle.crypto.engines.XSalsa20Engine;

import java.util.function.Supplier;

@AllArgsConstructor
public enum StreamCipherType {
    CHACHA10("ChaCha10", () -> new ChaChaEngine(10)),
    CHACHA20("ChaCha20", ChaChaEngine::new),
    CHACHA40("ChaCha40", () -> new ChaChaEngine(40)),
    GRAIN128("Grain-128", Grain128Engine::new),
    SALSA10("Salsa10", () -> new Salsa20Engine(10)),
    SALSA20("Salsa20", Salsa20Engine::new),
    XSALSA20("XSalsa20", XSalsa20Engine::new);

    public final String name;
    private final Supplier<StreamCipher> cipherSupplier;

    public StreamCipher create()   {
        return this.cipherSupplier.get();
    }
}
