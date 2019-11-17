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

package net.daporkchop.lib.http.response.aggregate;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.http.request.Request;
import net.daporkchop.lib.http.response.ResponseHeaders;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

/**
 * Aggregates received data by writing it to a file.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class ToFileAggregator implements ResponseAggregator<FileChannel, File> {
    @NonNull
    protected final File    file;
    protected final boolean allowOverwrite;

    public ToFileAggregator(@NonNull File file) {
        this(file, false);
    }

    @Override
    public FileChannel init(@NonNull ResponseHeaders response, @NonNull Request<File> request) throws Exception {
        PFiles.ensureDirectoryExists(this.file.getAbsoluteFile().getParentFile());
        FileChannel channel = this.allowOverwrite
                ? FileChannel.open(this.file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)
                : FileChannel.open(this.file.toPath(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        try {
            if (channel.tryLock() == null) {
                throw new IOException(String.format("Unable to acquire lock on file: \"%s\"", this.file.getAbsolutePath()));
            }
            return channel;
        } catch (Exception e) {
            channel.close(); //close channel immediately if an exception occurs
            throw e;
        }
    }

    @Override
    public FileChannel add(@NonNull FileChannel channel, @NonNull ByteBuf data, @NonNull Request<File> request) throws Exception {
        do {
            data.readBytes(channel, data.readableBytes());
        } while (data.isReadable());
        return channel;
    }

    @Override
    public File doFinal(@NonNull FileChannel temp, @NonNull Request<File> request) throws Exception {
        temp.close();
        return this.file;
    }

    @Override
    public void deinit(@NonNull FileChannel temp, @NonNull Request<File> request) throws Exception {
        temp.close();
    }
}
