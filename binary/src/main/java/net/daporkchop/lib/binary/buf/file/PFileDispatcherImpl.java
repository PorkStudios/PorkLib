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

package net.daporkchop.lib.binary.buf.file;

import net.daporkchop.lib.common.util.PorkUtil;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * {@link sun.nio.ch.FileDispatcherImpl} can kiss my ass
 *
 * @author DaPorkchop_
 */
public interface PFileDispatcherImpl {
    Class<?> FILEDISPATCHER_CLASS = PorkUtil.classForName("sun.nio.ch.FileDispatcherImpl");

    Read0 READ0 = PorkUtil.getLambdaReflection(Read0.class, FILEDISPATCHER_CLASS, true, false, int.class, "read0", FileDescriptor.class, long.class, int.class);

    @FunctionalInterface
    interface Read0 {
        /**
         * Reads bytes from a file into memory
         *
         * @param fd      the file descriptor to read from
         * @param address the address in memory to read to
         * @param len     the number of bytes to read
         * @return the number of bytes actually read
         * @throws IOException if an IO exception occurs you dummy
         * @see sun.nio.ch.FileDispatcherImpl#read(FileDescriptor, long, int)
         * @see sun.nio.ch.FileDispatcherImpl#read0(FileDescriptor, long, int)
         */
        int read(FileDescriptor fd, long address, int len) throws IOException;
    }
}
