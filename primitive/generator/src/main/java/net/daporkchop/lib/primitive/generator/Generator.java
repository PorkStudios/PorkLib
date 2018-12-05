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

package net.daporkchop.lib.primitive.generator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.logging.Logging;
import sun.misc.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static java.lang.Math.max;
import static net.daporkchop.lib.primitive.generator.Primitive.FULLNAME_DEF;
import static net.daporkchop.lib.primitive.generator.Primitive.GENERIC_EXTENDS_DEF;
import static net.daporkchop.lib.primitive.generator.Primitive.GENERIC_HEADER_DEF;
import static net.daporkchop.lib.primitive.generator.Primitive.GENERIC_SUPER_DEF;
import static net.daporkchop.lib.primitive.generator.Primitive.HEADERS_DEF;
import static net.daporkchop.lib.primitive.generator.Primitive.IMPORTS_DEF;
import static net.daporkchop.lib.primitive.generator.Primitive.LICENSE_DEF;
import static net.daporkchop.lib.primitive.generator.Primitive.METHODS_DEF;
import static net.daporkchop.lib.primitive.generator.Primitive.PACKAGE_DEF;
import static net.daporkchop.lib.primitive.generator.Primitive.primitives;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class Generator implements Logging {
    public static final AtomicLong FILES = new AtomicLong(0L);
    public static final AtomicLong SIZE = new AtomicLong(0L);
    private static final Collection<String> TREE_ROOTS = Arrays.asList(
            "main",
            "test"
    );
    public static String LICENSE;

    static {
        primitives.add(
                new Primitive()
                        .setFullName("Boolean")
                        .setName("boolean")
                        .setHashCode("x ? 1 : 0")
                        .setEmptyValue("false")
                        .setEquals("a == b")
                        .setStringFormat("%b")
        );
        primitives.add(
                new Primitive()
                        .setFullName("Byte")
                        .setName("byte")
                        .setHashCode("x & 0xFF")
                        .setEmptyValue("Byte.MIN_VALUE")
                        .setEquals("a == b")
                        .setStringFormat("%d")
        );
        primitives.add(
                new Primitive()
                        .setFullName("Character")
                        .setName("char")
                        .setHashCode("(x >> 24) ^ (x >> 16) ^ (x >> 8) ^ x")
                        .setEmptyValue("Character.MAX_VALUE")
                        .setEquals("a == b")
                        .setSerializationName("Char")
                        .setStringFormat("%c")
        );
        primitives.add(
                new Primitive()
                        .setFullName("Short")
                        .setName("short")
                        .setHashCode("(x >> 8) ^ x")
                        .setEmptyValue("Short.MIN_VALUE")
                        .setEquals("a == b")
                        .setStringFormat("%d")
        );
        primitives.add(
                new Primitive()
                        .setFullName("Integer")
                        .setName("int")
                        .setHashCode("(x >> 24) ^ (x >> 16) ^ (x >> 8) ^ x")
                        .setEmptyValue("Integer.MIN_VALUE")
                        .setEquals("a == b")
                        .setSerializationName("Int")
                        .setStringFormat("%d")
        );
        primitives.add(
                new Primitive()
                        .setFullName("Long")
                        .setName("long")
                        .setHashCode("hashInteger((int) (x >> 32)) ^ hashInteger((int) x)")
                        .setEmptyValue("Long.MIN_VALUE")
                        .setEquals("a == b")
                        .setStringFormat("%d")
        );
        primitives.add(
                new Primitive()
                        .setFullName("Float")
                        .setName("float")
                        .setHashCode("hashInteger(Float.floatToIntBits(x))")
                        .setEmptyValue("Float.NaN")
                        .setEquals("a == b")
                        .setStringFormat("%f")
        );
        primitives.add(
                new Primitive()
                        .setFullName("Double")
                        .setName("double")
                        .setHashCode("hashLong(Double.doubleToLongBits(x))")
                        .setEmptyValue("Double.NaN")
                        .setEquals("a == b")
                        .setStringFormat("%f")
        );
        primitives.add(
                new Primitive()
                        .setFullName("Object")
                        .setName("Object")
                        .setHashCode("Objects.hashCode(x)")
                        .setGeneric()
                        .setEmptyValue("null")
                        .setEquals("Objects.equals(a, b)")
                        .setStringFormat("%s")
        );

        try (InputStream is = new FileInputStream(new File(".", "../../LICENSE"))) {
            LICENSE = String.format("/*\n * %s\n */",
                    new String(IOUtils.readFully(is, -1, false))
                            .replace("$today.year", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)))
                            /*.trim()*/.replaceAll("\n", "\n * "));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public final File inRoot;
    @NonNull
    public final File outRoot;
    private final List<String> importList = new ArrayList<>();
    private final Collection<String> existing = new ArrayDeque<>();
    private final Collection<String> generated = new ArrayDeque<>();
    private String imports;

    public static void main(String... args) throws IOException {
        /*Generator generator = new Generator(
                new File(".", "primitive/generator/src/main/resources"),
                new File(".", "primitive/src/main/java/net/daporkchop/lib/primitive")
        );*/
        for (String s : TREE_ROOTS) {
            Generator generator = new Generator(
                    new File(".", String.format("src/main/resources/%s/java/", s)),
                    new File(".", String.format("../src/%s/java/", s))
            );
            generator.generate();
        }

        //System.out.println("Generated " + FILES + " files, totalling " + SIZE + " bytes (" + (SIZE / 1024D / 1024D) + " megabytes)");
        System.out.printf(
                "Generated %d files, totalling %s bytes (%.2f megabytes)\n",
                FILES.get(),
                NumberFormat.getInstance(Locale.US).format(SIZE.get()),
                (double) SIZE.get() / 1024.0d / 1024.0d
        );
    }

    public void generate() {
        if (false && this.outRoot.exists()) {
            this.rmDir(this.outRoot);
            //TODO: only delete files that no longer need to be created
        }
        if (this.outRoot.exists()) {
            this.addAllExisting(this.outRoot);
        }
        if (!this.outRoot.exists() && !this.outRoot.mkdirs()) {
            throw new IllegalStateException();
        }

        this.getImports();

        this.generate(this.inRoot, this.outRoot);

        if (this.existing.size() > this.generated.size()) {
            System.out.printf("Existing: %d, generated: %d\n", this.existing.size(), this.generated.size());
            for (String s : new ArrayDeque<>(this.generated)) {
                int count = Primitive.countVariables(s);
                if (count == 0) {
                    this.generated.add(s.replaceAll("\\.template", ".java"));
                } else {
                    this.forEachPrimitiveRecursive(primitives -> {
                        String s1 = s;
                        for (int i = 0; i < primitives.length; i++) {
                            s1 = s1.replaceAll(String.format(FULLNAME_DEF, i), primitives[i].getFullName());
                        }
                        this.generated.add(s1.replaceAll("\\.template", ".java"));
                    }, count);
                }
            }
            this.existing.removeAll(this.generated);
            System.out.printf("Existing: %d, generated: %d\n", this.existing.size(), this.generated.size());
            AtomicLong deletedFiles = new AtomicLong(0L);
            AtomicLong deletedSize = new AtomicLong(0L);
            this.existing.parallelStream().forEach(s -> {
                File file = new File(this.outRoot, s.replaceAll("\\.", "/").replaceAll("/java", ".java"));
                deletedSize.addAndGet(file.length());
                if (!file.delete()) {
                    throw new IllegalStateException(String.format("Unable to delete file %s", file.getAbsoluteFile().getAbsolutePath()));
                }
                deletedFiles.incrementAndGet();
            });
            System.out.printf(
                    "Deleted %d old files, totalling %s bytes (%.2f megabytes)\n",
                    deletedFiles.get(),
                    NumberFormat.getInstance(Locale.US).format(deletedSize.get()),
                    deletedSize.get() / 1024.0d / 1024.0d
            );
        }
    }

    public void generate(@NonNull File file, @NonNull File out) {
        if (!file.exists()) {
            throw new IllegalStateException();
        }
        if (file.isDirectory()) {
            if (file.getName().endsWith("_methods")) {
                return;
            }
            if (!"java".equals(file.getName())) {
                out = new File(out, file.getName());
            }
            File[] files = file.listFiles();
            if (files == null) {
                throw new NullPointerException();
            }
            File realOut = out;
            Arrays.stream(files).parallel().forEach(f -> this.generate(f, realOut));
        } else if (file.getName().endsWith(".template")) {
            String name = file.getName();
            String packageName = this.getPackageName(file);
            int count;
            {
                count = Primitive.countVariables(name);
                int countUpper = Primitive.countVariables(name.toUpperCase());
                if (countUpper > count) {
                    for (int i = 0; ; i++) {
                        String s = String.format(FULLNAME_DEF.toLowerCase(), i);
                        if (name.contains(s)) {
                            name = name.replaceAll(s, "");
                        } else {
                            break;
                        }
                    }
                    count = countUpper;
                }
            }
            this.generated.add(String.format("%s.%s", packageName, name));
            long lastModified = file.lastModified();

            String[] methods = new String[0];
            File methodsDir = new File(file.getParentFile(), String.format("%s_methods", name.replaceAll(".template", "")));
            if (methodsDir.exists()) {
                if (!methodsDir.isDirectory()) {
                    throw new IllegalStateException();
                }
                File[] files = methodsDir.listFiles();
                if (files == null) {
                    throw new NullPointerException();
                }
                methods = new String[files.length];
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    lastModified = max(lastModified, f.lastModified());
                    try (InputStream is = new FileInputStream(f)) {
                        methods[i] = new String(IOUtils.readFully(is, -1, false));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            {
                String s = name.replaceAll(".template", ".java");
                for (int i = 0; i < count; i++) {
                    s = s.replaceAll(String.format(FULLNAME_DEF, i), "Byte");
                }
                File potentialOut = new File(out, s);
                if (potentialOut.exists() && potentialOut.lastModified() >= lastModified) {
                    System.out.printf("Skipping %s\n", name);
                    return;
                }
            }
            String content;
            try (InputStream is = new FileInputStream(file)) {
                content = new String(IOUtils.readFully(is, -1, false));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!out.exists() && !out.mkdirs()) {
                throw new IllegalStateException();
            }
            System.out.printf("Generating %s\n", name);
            this.populateToDepth(out, name, content, String.format("package %s;", packageName), methods, count);
        }
    }

    private void populateToDepth(@NonNull File path, @NonNull String name, @NonNull String content, @NonNull String packageName, @NonNull String[] methods, int depth, Primitive... primitives) {
        if (depth > primitives.length) {
            Primitive[] p = new Primitive[primitives.length + 1];
            System.arraycopy(primitives, 0, p, 0, primitives.length);
            Primitive.primitives.forEach(primitive -> {
                p[p.length - 1] = primitive;
                this.populateToDepth(path, name, content, packageName, methods, depth, p);
            });
        } else {
            String nameOut = name.replaceAll(".template", ".java");
            String contentOut = content;

            if (methods.length != 0) {
                StringBuilder builder = new StringBuilder();
                for (String s : methods) {
                    this.forEachPrimitiveRecursive(primitives1 -> {
                        String in = s;
                        for (int i = primitives.length - 1; i >= 0; i--) {
                            in = primitives[i].format(in, i, false);
                        }
                        in = in
                                .replaceAll("_method", "_")
                                .replaceAll("<method%", "<%");
                        AtomicReference<String> ref = new AtomicReference<>(in);
                        for (int i = 0; i < primitives1.length; i++) {
                            ref.set(primitives1[i].format(ref.get(), i));
                        }
                        builder.append(ref.get()
                                .replaceAll(GENERIC_HEADER_DEF, Primitive.getGenericHeader(primitives1))
                                .replaceAll(GENERIC_SUPER_DEF, Primitive.getGenericSuper(primitives1))
                                .replaceAll(GENERIC_EXTENDS_DEF, Primitive.getGenericExtends(primitives1)));
                    }, depth);
                }
                contentOut = contentOut.replaceAll(METHODS_DEF, builder.toString());
            }

            if (depth == 0) {
                int i = 0;
                for (Primitive p : Primitive.primitives) {
                    nameOut = p.format(nameOut, i);
                    contentOut = p.format(contentOut, i++);
                }
            } else {
                for (int i = primitives.length - 1; i >= 0; i--) {
                    Primitive p = primitives[i];
                    nameOut = p.format(nameOut, i);
                    contentOut = p.format(contentOut, i);
                }
            }
            File file = new File(path, nameOut);

            contentOut = contentOut
                    .replaceAll(GENERIC_HEADER_DEF, Primitive.getGenericHeader(primitives))
                    .replaceAll(GENERIC_SUPER_DEF, Primitive.getGenericSuper(primitives))
                    .replaceAll(GENERIC_EXTENDS_DEF, Primitive.getGenericExtends(primitives))
                    .replaceAll(HEADERS_DEF, String.format("%s\n\n%s\n\n%s", LICENSE_DEF, PACKAGE_DEF, IMPORTS_DEF))
                    .replaceAll(PACKAGE_DEF, packageName)
                    .replaceAll(IMPORTS_DEF, this.imports)
                    .replaceAll(LICENSE_DEF, LICENSE);

            try (OutputStream os = new FileOutputStream(file)) {
                byte[] b = contentOut.getBytes(UTF8.utf8);
                os.write(b);
                SIZE.addAndGet(b.length);
                FILES.incrementAndGet();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!file.setLastModified(System.currentTimeMillis())) {
                throw new IllegalStateException();
            }
        }
    }

    private void rmDir(@NonNull File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                throw new NullPointerException();
            }
            Arrays.stream(files).parallel().forEach(this::rmDir);
        }
        if (!file.delete()) {
            throw new IllegalStateException();
        }
    }

    private String getPackageName(@NonNull File file) {
        List<String> list = new ArrayList<>();
        if (!file.isDirectory()) {
            file = file.getParentFile();
        }
        String name;
        while (!"java".equals(name = file.getName())) {
            list.add(0, name);
            file = file.getParentFile();
        }
        StringJoiner joiner = new StringJoiner(".");
        list.forEach(joiner::add);
        return joiner.toString();
    }

    private void getImports() {
        if (this.imports == null) {
            this.importList.clear();
            Collections.addAll(
                    this.importList,
                    "lombok.*",
                    "java.util.*",
                    "java.util.concurrent.*",
                    "java.util.concurrent.atomic.*",
                    "java.util.concurrent.locks.*",
                    "java.io.*",
                    "java.nio.*"
            );
            this.getImportsRecursive(this.inRoot);
            this.importList.sort(String::compareTo);

            StringBuilder builder = new StringBuilder();
            for (String s : this.importList) {
                builder.append(String.format("import %s;\n", s));
            }
            String s = builder.toString();
            this.imports = s.substring(0, s.length() - 1);
        }
    }

    private void getImportsRecursive(@NonNull File file) {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException();
        }
        File[] files = file.listFiles();
        if (files == null) {
            throw new NullPointerException();
        }
        boolean flag = true;
        for (File f : files) {
            if (f.isDirectory()) {
                if (!f.getName().endsWith("_methods")) {
                    this.getImportsRecursive(f);
                }
            } else if (flag && f.getName().endsWith(".template")) {
                this.importList.add(String.format("%s.*", this.getPackageName(f)));
                flag = false;
            }
        }
    }

    private void addAllExisting(@NonNull File file) {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException();
        }
        File[] files = file.listFiles();
        if (files == null) {
            throw new NullPointerException();
        }
        for (File f : files) {
            if (f.isDirectory()) {
                this.addAllExisting(f);
            } else {
                this.existing.add(String.format("%s.%s", this.getPackageName(f), f.getName()));
            }
        }
    }

    private void forEachPrimitiveRecursive(@NonNull Consumer<Primitive[]> consumer, int depth, Primitive... primitives) {
        if (depth > primitives.length) {
            Primitive[] p = new Primitive[primitives.length + 1];
            System.arraycopy(primitives, 0, p, 0, primitives.length);
            Primitive.primitives.forEach(primitive -> {
                p[p.length - 1] = primitive;
                this.forEachPrimitiveRecursive(consumer, depth, p);
            });
        } else {
            consumer.accept(primitives);
        }
    }
}
