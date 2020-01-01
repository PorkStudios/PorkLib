/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.binary.oio.writer;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.oio.PAppendable;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.common.util.exception.file.NoSuchFileException;
import net.daporkchop.lib.common.util.exception.file.NotAFileException;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.SoftReference;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;

/**
 * Equivalent to {@link java.io.FileWriter}, but uses the UTF-8 charset instead of the system default.
 *
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
        } catch (FileNotFoundException e)   {
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
        super.write(PorkUtil.unwrap(str));
    }

    @Override
    public void write(@NonNull String str, int off, int len) throws IOException {
        super.write(PorkUtil.unwrap(str), off, len);
    }

    @Override
    public Writer append(CharSequence seq) throws IOException {
        if (seq == null)    {
            this.write("null");
        } else if (seq instanceof String) {
            this.write((String) seq);
        } else {
            if (seq instanceof StringBuilder)   {
                this.write(PorkUtil.unwrap((StringBuilder) seq), 0, seq.length());
            } else if (seq instanceof StringBuffer)   {
                this.write(PorkUtil.unwrap((StringBuffer) seq), 0, seq.length());
            } else {
                synchronized (this) {
                    for (int i = 0, size = seq.length(); i < size; i++) {
                        super.write(seq.charAt(i));
                    }
                }
            }
        }
        return this;
    }

    @Override
    public Writer append(CharSequence seq, int start, int end) throws IOException {
        if (seq == null)    {
            this.write("null", start, end);
        } else if (seq instanceof String)   {
            this.write((String) seq, start, end);
        } else {
            PorkUtil.assertInRange(seq.length(), start, end);
            if (seq instanceof StringBuilder)   {
                this.write(PorkUtil.unwrap((StringBuilder) seq), start, end);
            } else if (seq instanceof StringBuffer)   {
                this.write(PorkUtil.unwrap((StringBuffer) seq), start, end);
            } else {
                synchronized (this) {
                    for (int i = start; i < end; i++) {
                        super.write(seq.charAt(i));
                    }
                }
            }
        }
        return this;
    }

    @Override
    public synchronized PAppendable appendLn(CharSequence seq) throws IOException {
        this.append(seq);
        return this.ln();
    }

    @Override
    public synchronized PAppendable appendLn(CharSequence seq1, CharSequence seq2) throws IOException {
        this.append(seq1);
        this.append(seq2);
        return this.ln();
    }

    @Override
    public synchronized PAppendable appendLn(CharSequence seq1, CharSequence seq2, CharSequence seq3) throws IOException {
        this.append(seq1);
        this.append(seq2);
        this.append(seq3);
        return this.ln();
    }

    @Override
    public synchronized PAppendable appendLn(CharSequence... sequences) throws IOException {
        for (CharSequence seq : sequences)  {
            this.append(seq);
        }
        return this.ln();
    }

    @Override
    public synchronized PAppendable appendLn(CharSequence seq, int start, int end) throws IOException, IndexOutOfBoundsException {
        this.append(seq, start, end);
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

    private UTF8FileWriter ln() throws IOException   {
        this.write(this.lineEnding);
        if (this.autoFlush) {
            this.flush();
        }
        return this;
    }
}
