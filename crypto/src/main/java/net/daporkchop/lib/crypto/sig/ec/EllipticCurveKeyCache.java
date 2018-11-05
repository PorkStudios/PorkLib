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

package net.daporkchop.lib.crypto.sig.ec;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.crypto.key.EllipticCurveKeyPair;
import net.daporkchop.lib.crypto.keygen.KeyGen;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EllipticCurveKeyCache {
    private static final Map<CurveType, CompletableFuture<EllipticCurveKeyPair>> cache = new EnumMap<>(CurveType.class);

    public static CompletableFuture<EllipticCurveKeyPair> getFutureKeyPair(@NonNull CurveType curveType)    {
        synchronized (cache)    {
            if (cache.containsKey(curveType))   {
                return cache.get(curveType);
            } else {
                CompletableFuture<EllipticCurveKeyPair> future = new CompletableFuture<>();
                cache.put(curveType, future);
                ForkJoinPool.commonPool().submit(() -> future.complete(KeyGen.gen(curveType)));
                return future;
            }
        }
    }

    public static EllipticCurveKeyPair getKeyPair(@NonNull CurveType type)  {
        try {
            return getFutureKeyPair(type).get();
        } catch (Exception e)   {
            throw new RuntimeException(e);
        }
    }
}
