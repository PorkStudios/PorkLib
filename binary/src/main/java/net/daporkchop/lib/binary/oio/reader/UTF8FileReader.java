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

package net.daporkchop.lib.binary.oio.reader;

import lombok.NonNull;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.util.exception.file.NoSuchFileException;
import net.daporkchop.lib.common.util.exception.file.NotAFileException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Equivalent to {@link java.io.FileReader}, but uses the UTF-8 charset instead of the system default.
 *
 * Uses a {@link sun.nio.cs.StreamDecoder#DEFAULT_BYTE_BUFFER_SIZE}-byte buffer.
 *
 * @author DaPorkchop_
 */
public final class UTF8FileReader extends InputStreamReader {
    private static FileInputStream wrap(@NonNull File file) throws NoSuchFileException, NotAFileException  {
        try {
            return new FileInputStream(PFiles.assertFileExists(file));
        } catch (FileNotFoundException e)   {
            throw new NoSuchFileException(file);
        }
    }

    public UTF8FileReader(String path) throws NoSuchFileException, NotAFileException {
        this(wrap(new File(path)));
    }

    public UTF8FileReader(File file) throws NoSuchFileException, NotAFileException {
        this(wrap(file));
    }

    public UTF8FileReader(InputStream in) {
        super(in, StandardCharsets.UTF_8);
    }
}
