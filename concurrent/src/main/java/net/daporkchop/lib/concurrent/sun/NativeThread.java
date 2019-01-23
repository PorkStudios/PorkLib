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

package net.daporkchop.lib.concurrent.sun;

/**
 * Wrapper around {@link sun.nio.ch.NativeThread} to prevent all the compile warnings
 *
 * @author DaPorkchop_
 */
public interface NativeThread {
    /**
     * Returns an opaque token representing the native thread underlying the
     * invoking Java thread. On systems that do not require signalling, this
     * method always returns -1.
     */
    static long current() {
        return -1L;
        //TODO: return sun.nio.ch.NativeThread.current();
    }

    /**
     * Signals the given native thread so as to release it from a blocking I/O
     * operation. On systems that do not require signalling, this method has
     * no effect.
     */
    static void signal(long id) {
        //TODO: sun.nio.ch.NativeThread.signal(id);
    }
}
