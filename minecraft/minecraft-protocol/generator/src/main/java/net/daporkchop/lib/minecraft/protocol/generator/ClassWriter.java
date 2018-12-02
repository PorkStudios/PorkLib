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
 */

package net.daporkchop.lib.minecraft.protocol.generator;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.binary.stream.StreamUtil;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author DaPorkchop_
 */
@Getter
public class ClassWriter implements Closeable {
    public static final Collection<String> GENERATED = new HashSet<>();
    public static final AtomicLong GENERATED_SIZE = new AtomicLong(0L);

    private static final byte[] SPACES = "    ".getBytes(UTF8.utf8);
    private static final byte[] NEWLINE = "\n".getBytes(UTF8.utf8);
    private static final byte[] OPEN_BRACES = " {".getBytes(UTF8.utf8);
    private static final byte[] CLOSE_BRACES = "}".getBytes(UTF8.utf8);

    private static final List<String> LICENSE;
    private static final List<String> HEADER;
    private static final Collection<String> DEFAULT_IMPORTS = Arrays.asList(
            "lombok.*",
            "java.util.*",
            "java.util.stream.*",
            "java.util.concurrent.*",
            "java.io.*",
            "java.nio.*",
            "net.daporkchop.lib.minecraft.protocol.api.*",
            "net.daporkchop.lib.minecraft.protocol.api.data.*"
    );

    static {
        List<String> license = null;
        List<String> header = null;
        try {
            try (BufferedReader in = new BufferedReader(new FileReader(new File(DataGenerator.IN_ROOT, "../templates/license.tmpl")))) {
                license = in.lines().collect(Collectors.toList());
            }
            try (BufferedReader in = new BufferedReader(new FileReader(new File(DataGenerator.IN_ROOT, "../templates/header.tmpl")))) {
                header = in.lines().collect(Collectors.toList());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            LICENSE = license;
            HEADER = header;
        }
    }

    private final File file;
    private final OutputStream out;
    private final Set<Integer> withBraces;
    private int indentCounter = 0;

    public ClassWriter(@NonNull File out, @NonNull String... imports) throws IOException {
        if (!out.getName().endsWith(".java"))   {
            out = new File(String.format("%s.java", out.getAbsolutePath()));
        }
        this.file = out;
        this.out = new BufferedOutputStream(new FileOutputStream(out));

        for (String s : LICENSE)    {
            this.write(s).newline();
        }
        String packageName = getPackageName(out);
        this.newline().write(String.format("package %s;", packageName));
        GENERATED.add(String.format("%s.%s", packageName, out.getName()));
        this.newline().newline();
        HashSet<String> theImports = new HashSet<>(DEFAULT_IMPORTS);
        for (String s : imports)    {
            if (s == null)  {
                throw new NullPointerException();
            }
            theImports.add(s);
        }
        theImports.stream().sorted().filter(s -> !s.isEmpty()).forEach(s -> {
            try {
                this.write(String.format("import %s;", s));
                this.newline();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        for (String s : HEADER) {
            this.newline().write(s);
        }

        this.withBraces = new HashSet<>();
    }

    private static boolean contains(@NonNull String s, int c) {
        for (int i = s.length() - 1; i >= 0; i--) {
            if ((int) s.charAt(i) == c) {
                return true;
            }
        }
        return false;
    }

    public static String getPackageName(@NonNull File file) {
        List<String> strings = new ArrayList<>();
        if (file.isFile()) {
            file = file.getParentFile();
        }
        while (!"src".equals(file.getName())) {
            strings.add(0, file.getName());
            file = file.getParentFile();
        }
        strings.remove(0);
        strings.remove(0);
        StringJoiner joiner = new StringJoiner(".");
        strings.forEach(joiner::add);
        return joiner.toString();
    }

    public ClassWriter pushBraces() throws IOException  {
        this.withBraces.add(++this.indentCounter);
        this.write(OPEN_BRACES);
        return this;
    }

    public ClassWriter newline() throws IOException {
        this.write(NEWLINE);
        return this;
    }

    public ClassWriter push() {
        this.indentCounter++;
        return this;
    }

    public ClassWriter pop() throws IOException {
        if (this.withBraces.remove(this.indentCounter--))   {
            this.indent();
            this.write(CLOSE_BRACES);
        }
        return this;
    }

    public ClassWriter write(@NonNull String s) throws IOException {
        if (contains(s, (int) '\n')) {
            this.write(s.split("\n"));
        } else {
            if (this.withBraces != null)    {
                this.indent();
            }
            this.write(s.getBytes(UTF8.utf8));
        }
        return this;
    }

    public ClassWriter write(@NonNull String... strings) throws IOException {
        for (String s : strings)    {
            this.write(s);
        }
        return this;
    }

    public ClassWriter write(@NonNull byte[] b) throws IOException {
        this.out.write(b);
        return this;
    }

    @Override
    public void close() throws IOException {
        while (this.indentCounter > 0)  {
            this.pop();
        }
        this.newline();
        this.out.close();
        GENERATED_SIZE.addAndGet(this.file.length());
    }

    private void indent() throws IOException    {
        this.newline();
        for (int i = this.indentCounter - 1; i >= 0; i--) {
            this.write(SPACES);
        }
    }

    @RequiredArgsConstructor
    @Getter
    private static class Data {
        @NonNull
        private final String header;
    }
}
