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

package net.daporkchop.lib.crypto.key.ec.impl;

import lombok.NonNull;
import net.daporkchop.lib.crypto.key.ec.AbstractECKeyPair;
import net.daporkchop.lib.crypto.keygen.ec.ECDHKeyGen;
import net.daporkchop.lib.crypto.sig.ec.CurveType;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class ECDHKeyPair extends AbstractECKeyPair {
    private static final Map<CurveType, CompletableFuture<ECDHKeyPair>> keyInstances = new EnumMap<>(CurveType.class);

    public static CompletableFuture<ECDHKeyPair> getKeyFuture(@NonNull CurveType type)  {
        synchronized (keyInstances) {
            return keyInstances.computeIfAbsent(type, c -> {
                CompletableFuture<ECDHKeyPair> future = new CompletableFuture<>();
                ForkJoinPool.commonPool().execute(() -> future.complete(ECDHKeyGen.gen(c)));
                return future;
            });
        }
    }

    public static ECDHKeyPair getKey(@NonNull CurveType type)   {
        CompletableFuture<ECDHKeyPair> future = getKeyFuture(type);
        try {
            return future.get();
        } catch (Exception e)   {
            throw new RuntimeException(e);
        }
    }

    public ECDHKeyPair(PrivateKey privateKey, PublicKey publicKey) {
        super(privateKey, publicKey);
    }
}
