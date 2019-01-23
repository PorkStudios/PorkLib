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
import net.daporkchop.lib.reflection.lambda.LambdaBuilder;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * {@link sun.nio.ch.FileDispatcherImpl} can kiss my ass
 *
 * @author DaPorkchop_
 */
public interface PFileDispatcherImpl {
    Class<?> FILEDISPATCHER_CLASS = PorkUtil.classForName("sun.nio.ch.FileDispatcherImpl");

    Read0 READ0 = LambdaBuilder.of(Read0.class)
            .setMethodHolder(FILEDISPATCHER_CLASS)
            .setMethodName("read0")
            .setStatic()
            .setInterfaceName("read")
            .returnType().setType(int.class).build()
            .param().setType(FileDescriptor.class).build()
            .param().setType(long.class).build()
            .param().setType(int.class).build()
            .build();

    PRead0 PREAD0 = LambdaBuilder.of(PRead0.class)
            .setMethodHolder(FILEDISPATCHER_CLASS)
            .setMethodName("pread0")
            .setStatic()
            .setInterfaceName("pread")
            .returnType().setType(int.class).build()
            .param().setType(FileDescriptor.class).build()
            .param().setType(long.class).build()
            .param().setType(int.class).build()
            .param().setType(long.class).build()
            .build();

    Write0 WRITE0 = LambdaBuilder.of(Write0.class)
            .setMethodHolder(FILEDISPATCHER_CLASS)
            .setMethodName("write0")
            .setStatic()
            .setInterfaceName("write")
            .returnType().setType(int.class).build()
            .param().setType(FileDescriptor.class).build()
            .param().setType(long.class).build()
            .param().setType(int.class).build()
            .setFallback(() -> LambdaBuilder.of(Write0.class) //windows implementation has an extra boolean parameter
                    .setMethodHolder(FILEDISPATCHER_CLASS)
                    .setMethodName("write0")
                    .setStatic()
                    .setInterfaceName("write")
                    .returnType().setType(int.class).build()
                    .param().setType(FileDescriptor.class).build()
                    .param().setType(long.class).build()
                    .param().setType(int.class).build()
                    .param().setType(boolean.class).build()
                    .build())
            .build();

    PWrite0 PWRITE0 = LambdaBuilder.of(PWrite0.class)
            .setMethodHolder(FILEDISPATCHER_CLASS)
            .setMethodName("pwrite0")
            .setStatic()
            .setInterfaceName("pwrite")
            .returnType().setType(int.class).build()
            .param().setType(FileDescriptor.class).build()
            .param().setType(long.class).build()
            .param().setType(int.class).build()
            .param().setType(long.class).build()
            .build();

    Force0 FORCE0 = LambdaBuilder.of(Force0.class)
            .setMethodHolder(FILEDISPATCHER_CLASS)
            .setMethodName("force0")
            .setStatic()
            .setInterfaceName("force")
            .returnType().setType(int.class).build()
            .param().setType(FileDescriptor.class).build()
            .param().setType(boolean.class).build()
            .build();

    Truncate0 TRUNCATE0 = LambdaBuilder.of(Truncate0.class)
            .setMethodHolder(FILEDISPATCHER_CLASS)
            .setMethodName("truncate0")
            .setStatic()
            .setInterfaceName("truncate")
            .returnType().setType(int.class).build()
            .param().setType(FileDescriptor.class).build()
            .param().setType(long.class).build()
            .build();

    Size0 SIZE0 = LambdaBuilder.of(Size0.class)
            .setMethodHolder(FILEDISPATCHER_CLASS)
            .setMethodName("size0")
            .setStatic()
            .setInterfaceName("size")
            .returnType().setType(long.class).build()
            .param().setType(FileDescriptor.class).build()
            .build();

    Lock0 LOCK0 = LambdaBuilder.of(Lock0.class)
            .setMethodHolder(FILEDISPATCHER_CLASS)
            .setMethodName("lock0")
            .setStatic()
            .setInterfaceName("lock")
            .returnType().setType(int.class).build()
            .param().setType(FileDescriptor.class).build()
            .param().setType(boolean.class).build()
            .param().setType(long.class).build()
            .param().setType(long.class).build()
            .param().setType(boolean.class).build()
            .build();

    Release0 RELEASE0 = LambdaBuilder.of(Release0.class)
            .setMethodHolder(FILEDISPATCHER_CLASS)
            .setMethodName("release0")
            .setStatic()
            .setInterfaceName("release")
            .returnType().setType(void.class).build()
            .param().setType(FileDescriptor.class).build()
            .param().setType(long.class).build()
            .param().setType(long.class).build()
            .build();

    Close0 CLOSE0 = LambdaBuilder.of(Close0.class)
            .setMethodHolder(FILEDISPATCHER_CLASS)
            .setMethodName("close0")
            .setStatic()
            .setInterfaceName("close")
            .returnType().setType(void.class).build()
            .param().setType(FileDescriptor.class).build()
            .build();

    PreClose0 PRECLOSE0 = LambdaBuilder.of(PreClose0.class)
            .setMethodHolder(FILEDISPATCHER_CLASS)
            .setMethodName("preClose0")
            .setStatic()
            .setInterfaceName("preClose")
            .returnType().setType(void.class).build()
            .param().setType(FileDescriptor.class).build()
            .setFallback(() -> fd -> {}) //this method doesn't exist on windows, so we have it default to doing nothing
            .build();

    CloseIntFD CLOSEINTFD = LambdaBuilder.of(CloseIntFD.class)
            .setMethodHolder(FILEDISPATCHER_CLASS)
            .setMethodName("closeIntFD")
            .setStatic()
            .returnType().setType(void.class).build()
            .param().setType(int.class).build()
            .setFallback(() -> fd -> {}) //this method doesn't exist on windows, so we have it default to doing nothing
            .build();

    @FunctionalInterface
    interface Read0 {
        /**
         * Reads bytes from a file into memory
         *
         * @param fd      the file descriptor to read from
         * @param address the address in memory to read into
         * @param len     the number of bytes to read
         * @return the number of bytes actually read
         * @throws IOException if an IO exception occurs you dummy
         * @see sun.nio.ch.FileDispatcherImpl#read(FileDescriptor, long, int)
         * @see sun.nio.ch.FileDispatcherImpl#read0(FileDescriptor, long, int)
         */
        int read(FileDescriptor fd, long address, int len) throws IOException;
    }

    @FunctionalInterface
    interface PRead0 {
        /**
         * Reads bytes from a given position in a file file into memory
         *
         * @param fd       the file descriptor to read from
         * @param address  the address in memory to read into
         * @param len      the number of bytes to read
         * @param position the position in the file to read at
         * @return the number of bytes actually read
         * @throws IOException if an IO exception occurs you dummy
         * @see sun.nio.ch.FileDispatcherImpl#pread(FileDescriptor, long, int, long)
         * @see sun.nio.ch.FileDispatcherImpl#pread0(FileDescriptor, long, int, long)
         */
        int pread(FileDescriptor fd, long address, int len, long position) throws IOException;
    }

    //@FunctionalInterface
    interface Write0 {
        /**
         * Writes bytes from memory to a file
         *
         * @param fd      the file descriptor to write to
         * @param address the address in memory to write bytes from
         * @param len     the number of bytes to write
         * @return the number of bytes actually written
         * @throws IOException if an IO exception occurs you dummy
         * @see sun.nio.ch.FileDispatcherImpl#write(FileDescriptor, long, int)
         * @see sun.nio.ch.FileDispatcherImpl#write0(FileDescriptor, long, int)
         */
        default int write(FileDescriptor fd, long address, int len) throws IOException {
            return this.write(fd, address, len, false);
        }

        /**
         * Writes bytes from memory to a file
         *
         * @param fd      the file descriptor to write to
         * @param address the address in memory to write bytes from
         * @param len     the number of bytes to write
         * @param append  whether or not to append to the file
         * @return the number of bytes actually written
         * @throws IOException if an IO exception occurs you dummy
         * @see sun.nio.ch.FileDispatcherImpl#write(FileDescriptor, long, int)
         * @see sun.nio.ch.FileDispatcherImpl#write0(FileDescriptor, long, int, boolean)
         */
        default int write(FileDescriptor fd, long address, int len, boolean append) throws IOException {
            return this.write(fd, address, len);
        }
    }

    @FunctionalInterface
    interface PWrite0 {
        /**
         * Writes bytes from memory to a given position in a file
         *
         * @param fd       the file descriptor to write to
         * @param address  the address in memory to write bytes from
         * @param len      the number of bytes to write
         * @param position the position in the file to write at
         * @return the number of bytes actually written
         * @throws IOException if an IO exception occurs you dummy
         * @see sun.nio.ch.FileDispatcherImpl#pwrite(FileDescriptor, long, int, long)
         * @see sun.nio.ch.FileDispatcherImpl#pwrite0(FileDescriptor, long, int, long)
         */
        int pwrite(FileDescriptor fd, long address, int len, long position) throws IOException;
    }

    @FunctionalInterface
    interface Force0 {
        /**
         * Force changes to a file to be written to disk
         *
         * @param fd       the file descriptor whose contents need to be forced
         * @param metaData whether or not file metadata will be written
         * @return the exit status? i think
         * @throws IOException if an IO exception occurs you dummy
         * @see sun.nio.ch.FileDispatcherImpl#force(FileDescriptor, boolean)
         * @see sun.nio.ch.FileDispatcherImpl#force0(FileDescriptor, boolean)
         */
        int force(FileDescriptor fd, boolean metaData) throws IOException;
    }

    @FunctionalInterface
    interface Truncate0 {
        /**
         * Truncate a file down to a certain length. Only effective if the file's current length is less than the given
         * size, otherwise the results are not (well) defined.
         *
         * @param fd   the file descriptor whose contents need to be forced
         * @param size the new size of the file
         * @return the exit status? i think
         * @throws IOException if an IO exception occurs you dummy
         * @see sun.nio.ch.FileDispatcherImpl#truncate(FileDescriptor, long)
         * @see sun.nio.ch.FileDispatcherImpl#truncate0(FileDescriptor, long)
         */
        int truncate(FileDescriptor fd, long size) throws IOException;
    }

    @FunctionalInterface
    interface Size0 {
        /**
         * Gets the current length of a file
         *
         * @param fd the file descriptor whose name needs to be gotten
         * @return the size, or exit status
         * @throws IOException if an IO exception occurs you dummy
         * @see sun.nio.ch.FileDispatcherImpl#size(FileDescriptor)
         * @see sun.nio.ch.FileDispatcherImpl#size0(FileDescriptor)
         */
        long size(FileDescriptor fd) throws IOException;
    }

    @FunctionalInterface
    interface Lock0 {
        /**
         * Attempts to acquire a lock on a region of a file
         *
         * @param fd       the file descriptor of the file to lock
         * @param blocking whether or not the lock operation should block
         * @param pos      the position in the file to begin the lock at
         * @param size     the size of the region to lock (can be longer than the current length of the file, set to
         *                 {@link Long#MAX_VALUE} to lock the entire file)
         * @param shared   whether or not the lock will be shared. Not all systems support shared locks
         * @return the exit status? i think
         * @throws IOException if an IO exception occurs you dummy
         * @see sun.nio.ch.FileDispatcherImpl#lock(FileDescriptor, boolean, long, long, boolean)
         * @see sun.nio.ch.FileDispatcherImpl#lock0(FileDescriptor, boolean, long, long, boolean)
         */
        int lock(FileDescriptor fd, boolean blocking, long pos, long size, boolean shared) throws IOException;
    }

    @FunctionalInterface
    interface Release0 {
        /**
         * Releases a lock on a region of a file
         *
         * @param fd   the file descriptor of the file to release a lock on
         * @param pos  the position in the file to begin releasing lock on
         * @param size size of the region to unlock
         * @throws IOException if an IO exception occurs you dummy
         * @see sun.nio.ch.FileDispatcherImpl#release(FileDescriptor, long, long)
         * @see sun.nio.ch.FileDispatcherImpl#release0(FileDescriptor, long, long)
         */
        void release(FileDescriptor fd, long pos, long size) throws IOException;
    }

    @FunctionalInterface
    interface Close0 {
        /**
         * Closes an open file
         *
         * @param fd the file descriptor of the file to close
         * @throws IOException if an IO exception occurs you dummy
         * @see sun.nio.ch.FileDispatcherImpl#close(FileDescriptor)
         * @see sun.nio.ch.FileDispatcherImpl#close0(FileDescriptor)
         */
        void close(FileDescriptor fd) throws IOException;
    }

    @FunctionalInterface
    interface PreClose0 {
        /**
         * Called before closing a file (I think?)
         *
         * @param fd the file descriptor of the file to close
         * @throws IOException if an IO exception occurs you dummy
         * @see sun.nio.ch.FileDispatcherImpl#preClose(FileDescriptor)
         * @see sun.nio.ch.FileDispatcherImpl#preClose0(FileDescriptor)
         */
        void preClose(FileDescriptor fd) throws IOException;
    }

    @FunctionalInterface
    interface CloseIntFD {
        /**
         * Closes a file using a 32-bit file descriptor
         *
         * @param fd the file descriptor of the file to close
         * @throws IOException if an IO exception occurs you dummy
         * @see sun.nio.ch.FileDispatcherImpl#closeIntFD(int)
         */
        void closeIntFD(int fd) throws IOException;
    }
}
