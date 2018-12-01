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
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DaPorkchop_
 */
@Getter
public class ClassWriter implements Closeable {
    private static final byte[] SPACES = "    ".getBytes(UTF8.utf8);
    private static final byte[] NEWLINE = "\n".getBytes(UTF8.utf8);
    private static final Map<String, Data> DATA_CAHE = new ConcurrentHashMap<>();

    private final OutputStream out;
    private final String templateName;
    private final Data data;
    private int indentCounter = 0;

    public ClassWriter(@NonNull File out, @NonNull String templateName) throws IOException {
        this.out = new BufferedOutputStream(new FileOutputStream(out));
        this.templateName = templateName;
        this.data = DATA_CAHE.computeIfAbsent(templateName, name -> {
            try {
                File root = new File(DataGenerator.IN_ROOT, String.format("../templates/%s/", name));

                String header;
                try (InputStream in = new FileInputStream(new File(root, "header.tmpl"))) {
                    header = new String(StreamUtil.readFully(in, -1, false), UTF8.utf8);
                }

                return new Data(header);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.write(this.data.header
                .replace("${package}", getPackageName(out)));
    }

    private static boolean contains(@NonNull String s, int c) {
        for (int i = s.length() - 1; i >= 0; i--) {
            if ((int) s.charAt(i) == c) {
                return true;
            }
        }
        return false;
    }

    private static String getPackageName(@NonNull File file) {
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

    public ClassWriter push() {
        this.indentCounter++;
        return this;
    }

    public ClassWriter pop() {
        this.indentCounter--;
        return this;
    }

    public ClassWriter write(@NonNull String s) throws IOException {
        if (contains(s, (int) '\n')) {
            for (String s1 : s.split("\n")) {
                this.write(s1);
            }
        } else {
            for (int i = this.indentCounter - 1; i >= 0; i--) {
                this.write(SPACES);
            }
            this.write(s.getBytes(UTF8.utf8));
            this.write(NEWLINE);
        }
        return this;
    }

    public ClassWriter write(@NonNull byte[] b) throws IOException {
        this.out.write(b);
        return this;
    }

    @Override
    public void close() throws IOException {
        this.out.close();
    }

    @RequiredArgsConstructor
    @Getter
    private static class Data {
        @NonNull
        private final String header;
    }
}
