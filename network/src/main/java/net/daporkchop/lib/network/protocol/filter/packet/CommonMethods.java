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

package net.daporkchop.lib.network.protocol.filter.packet;

import lombok.NonNull;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherHelper;
import net.daporkchop.lib.network.protocol.encapsulated.session.SessionData;
import org.apache.mina.core.session.IoSession;

import java.util.concurrent.CompletableFuture;

/**
 * @author DaPorkchop_
 */
interface CommonMethods {
    @SuppressWarnings("unchecked")
    default BlockCipherHelper getHelper(@NonNull IoSession session) {
        Object obj = SessionData.CIPHER_HELPER.get(session);
        if (obj instanceof CompletableFuture)   {
            try {
                obj = ((CompletableFuture<BlockCipherHelper>) obj).get();
            } catch (Throwable t)   {
                t.printStackTrace();
                throw new IllegalStateException("Unable to initialize cipher", t);
            }
            SessionData.CIPHER_HELPER.set(session, obj);
        }
        return (BlockCipherHelper) obj;
    }
}
