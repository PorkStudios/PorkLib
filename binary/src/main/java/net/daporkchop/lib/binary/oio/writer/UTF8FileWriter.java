/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.binary.oio.writer;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.oio.appendable.PAppendable;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.misc.string.PStrings;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.common.util.exception.file.NoSuchFileException;
import net.daporkchop.lib.common.util.exception.file.NotAFileException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.SoftReference;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;

/**
 * Equivalent to {@link java.io.FileWriter}, but uses the UTF-8 charset instead of the system default.
 * <p>
 * Uses a {@link sun.nio.cs.StreamDecoder#DEFAULT_BYTE_BUFFER_SIZE}-byte buffer.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class UTF8FileWriter extends OutputStreamWriter implements PAppendable {
    private static OutputStream wrap(@NonNull File file, boolean append) throws NoSuchFileException, NotAFileException {
        try {
            return new FileOutputStream(PFiles.assertFileExists(file), append);
        } catch (FileNotFoundException e) {
            throw new NoSuchFileException(file);
        }
    }

    private final String lineEnding;
    private volatile SoftReference<Formatter> formatterRef;
    private final boolean autoFlush;

    public UTF8FileWriter(String path) throws NoSuchFileException, NotAFileException {
        this(path, false);
    }

    public UTF8FileWriter(String path, boolean append) throws NoSuchFileException, NotAFileException {
        this(wrap(new File(path), append), PlatformInfo.OPERATING_SYSTEM.lineEnding(), false);
    }

    public UTF8FileWriter(File file) throws NoSuchFileException, NotAFileException {
        this(file, false);
    }

    public UTF8FileWriter(File file, boolean append) throws NoSuchFileException, NotAFileException {
        this(wrap(file, append), PlatformInfo.OPERATING_SYSTEM.lineEnding(), false);
    }

    public UTF8FileWriter(String path, @NonNull String lineEnding, boolean autoFlush) throws NoSuchFileException, NotAFileException {
        this(path, false, lineEnding, autoFlush);
    }

    public UTF8FileWriter(String path, boolean append, @NonNull String lineEnding, boolean autoFlush) throws NoSuchFileException, NotAFileException {
        this(wrap(new File(path), append), lineEnding, autoFlush);
    }

    public UTF8FileWriter(File file, @NonNull String lineEnding, boolean autoFlush) throws NoSuchFileException, NotAFileException {
        this(file, false, lineEnding, autoFlush);
    }

    public UTF8FileWriter(File file, boolean append, @NonNull String lineEnding, boolean autoFlush) throws NoSuchFileException, NotAFileException {
        this(wrap(file, append), lineEnding, autoFlush);
    }

    public UTF8FileWriter(OutputStream out, @NonNull String lineEnding, boolean autoFlush) {
        super(out, StandardCharsets.UTF_8);

        this.lineEnding = lineEnding;
        this.autoFlush = autoFlush;
    }

    //optimizations to OutputStreamWriter
    //these are actually really beneficial

    //note that a lot of synchronization is already done internally by StreamEncoder, using this as a mutex

    @Override
    public void write(@NonNull String str) throws IOException {
        this.write(PStrings.stringToImmutableArray(str), 0, str.length());
    }

    @Override
    public UTF8FileWriter append(CharSequence seq) throws IOException {
        char[] unwrapped = PStrings.tryCharSequenceToImmutableArray(seq).orElse(null);
        if (unwrapped != null) { //we were able to unwrap the sequence, write it as a simple array
            this.write(unwrapped, 0, seq.length());
        } else { //fall back to default implementation
            super.append(seq);
        }
        return this;
    }

    @Override
    public UTF8FileWriter append(CharSequence seq, int start, int end) throws IOException {
        char[] unwrapped = PStrings.tryCharSequenceToImmutableArray(seq).orElse(null);
        if (unwrapped != null) { //we were able to unwrap the sequence, write it as a simple array
            this.write(unwrapped, start, end);
        } else { //fall back to default implementation
            super.append(seq, start, end);
        }
        return this;
    }

    @Override
    public UTF8FileWriter append(char c) throws IOException {
        this.write(c);
        return this;
    }

    @Override
    public synchronized PAppendable appendLn() throws IOException {
        return this.ln();
    }

    @Override
    public synchronized PAppendable appendFmt(String format, Object... args) throws IOException {
        SoftReference<Formatter> ref = this.formatterRef;
        Formatter formatter;
        if (ref == null || (formatter = ref.get()) == null) {
            this.formatterRef = new SoftReference<>(formatter = new Formatter(this));
        }
        formatter.format(format, args);
        return this.ln();
    }

    private UTF8FileWriter ln() throws IOException {
        this.write(this.lineEnding);
        if (this.autoFlush) {
            this.flush();
        }
        return this;
    }
}
