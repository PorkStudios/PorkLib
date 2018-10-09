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
import sun.misc.IOUtils;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static net.daporkchop.lib.primitive.generator.Primitive.*;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class Generator {
    public static final AtomicLong FILES = new AtomicLong(0L);
    public static final AtomicLong SIZE = new AtomicLong(0L);
    public static String LICENSE;

    static {
        primitives.add(
                new Primitive()
                        .setFullName("Boolean")
                        .setName("boolean")
                        .setHashCode("x ? 1 : 0")
        );
        primitives.add(
                new Primitive()
                        .setFullName("Byte")
                        .setName("byte")
                        .setHashCode("x & 0xFF")
        );
        primitives.add(
                new Primitive()
                        .setFullName("Short")
                        .setName("short")
                        .setHashCode("(x >> 8) ^ x")
        );
        primitives.add(
                new Primitive()
                        .setFullName("Integer")
                        .setName("int")
                        .setHashCode("(x >> 24) ^ (x >> 16) ^ (x >> 8) ^ x")
        );
        primitives.add(
                new Primitive()
                        .setFullName("Long")
                        .setName("long")
                        .setHashCode("this.hashInteger((int) ((x >> 32) & 0xFFFFFFFF)) ^ this.hashInteger((int) x)")
        );
        primitives.add(
                new Primitive()
                        .setFullName("Float")
                        .setName("float")
                        .setHashCode("this.hashInteger(Float.floatToIntBits(x))")
        );
        primitives.add(
                new Primitive()
                        .setFullName("Long")
                        .setName("long")
                        .setHashCode("this.hashLong(Double.doubleToLongBits(x))")
        );
        primitives.add(
                new Primitive()
                        .setFullName("Object")
                        .setName("Object")
                        .setHashCode("java.util.Objects.hashCode(x)")
                        .setGeneric()
        );

        try (InputStream is = new FileInputStream(new File(".", "../../LICENSE"))) {
            byte[] b = IOUtils.readFully(is, -1, false);
            LICENSE = String.format("/*\n * %s\n */",
                    new String(b)
                            .replace("$today.year", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)))
                            /*.trim()*/.replaceAll("\n", "\n * "));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String... args) throws IOException {
        /*Generator generator = new Generator(
                new File(".", "primitive/generator/src/main/resources"),
                new File(".", "primitive/src/main/java/net/daporkchop/lib/primitive")
        );*/
        Generator generator = new Generator(
                new File(".", "src/main/resources"),
                new File(".", "../src/main/java")
                //new File(".", "../src/main/java/net/daporkchop/lib/primitive")
        );
        generator.generate();

        //System.out.println("Generated " + FILES + " files, totalling " + SIZE + " bytes (" + (SIZE / 1024D / 1024D) + " megabytes)");
        System.out.printf(
                "Generated %d files, totalling %s bytes (%.2f megabytes)\n",
                FILES.get(),
                NumberFormat.getInstance(Locale.US).format(SIZE.get()),
                SIZE.get() / 1024.0d / 1024.0d
        );
    }

    @NonNull
    public final File inRoot;
    @NonNull
    public final File outRoot;
    private final List<String> importList = new ArrayList<>();
    private final Collection<String> existing = new ArrayDeque<>();
    private final Collection<String> generated = new ArrayDeque<>();
    private String imports;

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

        if (this.existing.size() > this.generated.size())   {
            System.out.printf("Existing: %d, generated: %d\n", this.existing.size(), this.generated.size());
            for (String s : new ArrayDeque<>(this.generated))   {
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
            this.existing.forEach(s -> {
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
            if (!"resources".equals(file.getName())) {
                out = new File(out, file.getName());
            }
            File[] files = file.listFiles();
            if (files == null) {
                throw new NullPointerException();
            }
            for (File f : files) {
                this.generate(f, out);
            }
        } else if (file.getName().endsWith(".template")) {
            String name = file.getName();
            String packageName = this.getPackageName(file);
            this.generated.add(String.format("%s.%s", packageName, file.getName()));
            int count = Primitive.countVariables(name);
            {
                String s = name.replaceAll(".template", ".java");
                for (int i = 0; i < count; i++) {
                    s = s.replaceAll(String.format(FULLNAME_DEF, i), "Byte");
                }
                File potentialOut = new File(out, s);
                if (potentialOut.exists() && potentialOut.lastModified() >= file.lastModified()) {
                    System.out.printf("Skipping %s\n", name);
                    return;
                }
            }
            String content;
            try (InputStream is = new FileInputStream(file)) {
                byte[] b = IOUtils.readFully(is, -1, false);
                content = new String(b);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!out.exists() && !out.mkdirs()) {
                throw new IllegalStateException();
            }
            System.out.printf("Generating %s\n", name);
            this.populateToDepth(out, name, content, String.format("package %s;", packageName), count);
        }
    }

    private void populateToDepth(@NonNull File path, @NonNull String name, @NonNull String content, @NonNull String packageName, int depth, Primitive... primitives) {
        if (depth > primitives.length) {
            Primitive[] p = new Primitive[primitives.length + 1];
            System.arraycopy(primitives, 0, p, 0, primitives.length);
            Primitive.primitives.forEach(primitive -> {
                p[p.length - 1] = primitive;
                this.populateToDepth(path, name, content, packageName, depth, p);
            });
        } else {
            String nameOut = name.replaceAll(".template", ".java");
            String contentOut = content;
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
            //this.generated.add(String.format("%s.%s", packageName, file.getName()));
        }
    }

    private void rmDir(@NonNull File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                throw new NullPointerException();
            }
            for (File f : files) {
                this.rmDir(f);
            }
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
        while (!"resources".equals(name = file.getName()) && !"java".equals(name)) {
            list.add(0, name);
            file = file.getParentFile();
        }
        StringBuilder builder = new StringBuilder();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            builder.append(list.get(i));
            if (i + 1 != size) {
                builder.append('.');
            }
        }
        //String text = builder.toString();
        //System.out.println(text);
        //return text;
        return builder.toString();
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
                this.getImportsRecursive(f);
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
        for (File f : files)    {
            if (f.isDirectory())    {
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
