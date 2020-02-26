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

package net.daporkchop.lib.primitive.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.oio.StreamUtil;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.misc.InstancePool;
import net.daporkchop.lib.logging.Logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.Math.max;
import static net.daporkchop.lib.primitive.generator.Primitive.*;

import static net.daporkchop.lib.logging.Logging.*;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class Generator {
    public static final AtomicLong FILES = new AtomicLong(0L);
    public static final AtomicLong SIZE = new AtomicLong(0L);
    private static final Collection<Generator> GENERATORS = Stream.of(
            null
            , new Generator(
                    new File("src/main/resources/primitive/java"),
                    new File("../src/generated/java"),
                    Collections.emptyList())
            /*, new Generator(
                    new File("src/main/resources/test/java"),
                    new File("../src/test/java"),
                    Collections.singletonList("org.junit.*"))*/
            , new Generator(
                    new File("src/main/resources/lambda/java"),
                    new File("../lambda/src/generated/java"),
                    Collections.emptyList())
    ).filter(Objects::nonNull).collect(Collectors.toList());
    public static String LICENSE;
    private static final JsonArray EMPTY_JSON_ARRAY = new JsonArray();

    static {
        PRIMITIVES.add(
                new Primitive()
                        .setFullName("Boolean")
                        .setDisplayName("Bool")
                        .setName("boolean")
                        .setHashCode("$1 ? 1 : 0")
                        .setEmptyValue("false")
                        .setEquals("$1 == $2")
                        .setNequals("$1 != $2")
                        .build()
        );
        PRIMITIVES.add(
                new Primitive()
                        .setFullName("Byte")
                        .setName("byte")
                        .setHashCode("$1 & 0xFF")
                        .setEmptyValue("Byte.MIN_VALUE")
                        .setEquals("$1 == $2")
                        .setNequals("$1 != $2")
                        .build()
        );
        PRIMITIVES.add(
                new Primitive()
                        .setFullName("Character")
                        .setDisplayName("Char")
                        .setName("char")
                        .setHashCode("($1 >>> 8) ^ $1")
                        .setEmptyValue("(char) 65535")
                        .setEquals("$1 == $2")
                        .setNequals("$1 != $2")
                        .build()
        );
        PRIMITIVES.add(
                new Primitive()
                        .setFullName("Short")
                        .setName("short")
                        .setHashCode("($1 >>> 8) ^ $1")
                        .setEmptyValue("Short.MIN_VALUE")
                        .setEquals("$1 == $2")
                        .setNequals("$1 != $2")
                        .build()
        );
        PRIMITIVES.add(
                new Primitive()
                        .setFullName("Integer")
                        .setDisplayName("Int")
                        .setUnsafeName("Int")
                        .setName("int")
                        .setHashCode("($1 >>> 24) ^ ($1 >>> 16) ^ ($1 >>> 8) ^ $1")
                        .setEmptyValue("Integer.MIN_VALUE")
                        .setEquals("$1 == $2")
                        .setNequals("$1 != $2")
                        .build()
        );
        PRIMITIVES.add(
                new Primitive()
                        .setFullName("Long")
                        .setName("long")
                        .setHashCode("(int) (($1 >>> 56) ^ ($1 >>> 48) ^ ($1 >>> 40) ^ ($1 >>> 32) ^ ($1 >>> 24) ^ ($1 >>> 16) ^ ($1 >>> 8) ^ $1)")
                        .setEmptyValue("Long.MIN_VALUE")
                        .setEquals("$1 == $2")
                        .setNequals("$1 != $2")
                        .build()
        );
        PRIMITIVES.add(
                new Primitive()
                        .setFullName("Float")
                        .setName("float")
                        .setHashCode("Float.floatToIntBits($1)")
                        .setEmptyValue("Float.NaN")
                        .setEquals("$1 == $2")
                        .setNequals("$1 != $2")
                        .build()
        );
        PRIMITIVES.add(
                new Primitive()
                        .setFullName("Double")
                        .setName("double")
                        .setHashCode("(int) Double.doubleToLongBits($1)")
                        .setEmptyValue("Double.NaN")
                        .setEquals("$1 == $2")
                        .setNequals("$1 != $2")
                        .build()
        );
        PRIMITIVES.add(
                new Primitive()
                        .setFullName("Object")
                        .setDisplayName("Obj")
                        .setName("Object")
                        .setHashCode("java.util.Objects.hashCode($1)")
                        .setGeneric()
                        .setEmptyValue("null")
                        .setEquals("java.util.Objects.equals($1, $2)")
                        .setNequals("!java.util.Objects.equals($1, $2)")
                        .build()
        );

        try (InputStream is = new FileInputStream(new File(".", "../../LICENSE"))) {
            LICENSE = String.format("/*\n * %s\n */",
                    new String(StreamUtil.toByteArray(is))
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
        for (Generator generator : GENERATORS)  {
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

    @NonNull
    public final File inRoot;
    @NonNull
    public final File outRoot;
    @NonNull
    public final Collection<String> additionalImports;
    private final List<String> importList = new ArrayList<>();
    private final Collection<String> existing = new ArrayDeque<>();
    private final Collection<String> generated = new ArrayDeque<>();
    private String imports;

    public void generate() {
        if (false && this.outRoot.exists()) {
            PFiles.rmContentsParallel(this.outRoot);
            //TODO: only delete files that no longer need to be created
        }

            this.addAllExisting(PFiles.ensureDirectoryExists(this.outRoot));

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
                            s1 = s1.replaceAll(String.format(DISPLAYNAME_DEF, i), primitives[i].displayName);
                        }
                        this.generated.add(s1.replaceAll("\\.template", ".java"));
                    }, count, new JsonObject());
                }
            }
            this.existing.removeAll(this.generated);
            System.out.printf("Existing: %d, generated: %d\n", this.existing.size(), this.generated.size());
            AtomicLong deletedFiles = new AtomicLong(0L);
            AtomicLong deletedSize = new AtomicLong(0L);
            this.existing.stream().forEach(s -> {
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
            File realOut = out;
            Arrays.stream(file.listFiles()).forEach(f -> this.generate(f, realOut));
        } else if (file.getName().endsWith(".template")) {
            String name = file.getName();
            String packageName = this.getPackageName(file);
            int count;
            {
                count = Primitive.countVariables(name);
                int countUpper = Primitive.countVariables(name.toUpperCase());
                if (countUpper > count) {
                    for (int i = 0; ; i++) {
                        String s = String.format(DISPLAYNAME_DEF.toLowerCase(), i);
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
                File[] files = PFiles.ensureDirectoryExists(methodsDir).listFiles();
                methods = new String[files.length];
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    lastModified = max(lastModified, f.lastModified());
                    try (InputStream is = new FileInputStream(f)) {
                        methods[i] = new String(StreamUtil.toByteArray(is));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            {
                String s = name.replaceAll(".template", ".java");
                for (int i = 0; i < count; i++) {
                    s = s.replaceAll(String.format(DISPLAYNAME_DEF, i), "Byte");
                }
                File potentialOut = new File(out, s);
                if (potentialOut.exists() && potentialOut.lastModified() >= lastModified) {
                    System.out.printf("Skipping %s\n", name);
                    return;
                }
            }
            String content;
            try (InputStream is = new FileInputStream(file)) {
                content = new String(StreamUtil.toByteArray(is));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            PFiles.ensureDirectoryExists(out);
            JsonObject settings;
            {
                if (content.startsWith("$$$settings$$$")) {
                    String[] split = content.split("_headers_", 2);
                    content = "_headers_" + split[1];
                    try (Reader reader = new StringReader(split[0])) {
                        reader.skip("$$$settings$$$".length());
                        settings = InstancePool.getInstance(JsonParser.class).parse(reader).getAsJsonObject();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    settings = new JsonObject();
                }
            }
            System.out.printf("Generating %s\n", name);
            this.populateToDepth(out, name, content, String.format("package %s;", packageName), methods, count, settings, this.imports);
        }
    }

    private void populateToDepth(@NonNull File path, @NonNull String name, @NonNull String content, @NonNull String packageName, @NonNull String[] methods, int depth, @NonNull JsonObject settings, @NonNull String imports, Primitive... primitives) {
        if (depth > primitives.length) {
            Primitive[] p = new Primitive[primitives.length + 1];
            System.arraycopy(primitives, 0, p, 0, primitives.length);
            Set<String> valid = Primitive.PRIMITIVES.stream().map(pr -> pr.name).collect(Collectors.toSet());
            if (settings.has(String.format("P%d", primitives.length))) {
                JsonObject object = settings.getAsJsonObject(String.format("P%d", primitives.length));
                if (object.has("whitelist")) {
                    valid = StreamSupport.stream(object.getAsJsonArray("whitelist").spliterator(), true).map(JsonElement::getAsString).collect(Collectors.toSet());
                } else if (object.has("blacklist")) {
                    for (JsonElement element : object.getAsJsonArray("blacklist")) {
                        valid.remove(element.getAsString());
                    }
                }
            }
            for (Primitive primitive : Primitive.PRIMITIVES) {
                if (valid.contains(primitive.name)) {
                    p[p.length - 1] = primitive;
                    this.populateToDepth(path, name, content, packageName, methods, depth, settings, imports, p);
                }
            }
        } else {
            String nameOut = name.replace(".template", ".java");
            String contentOut = content;

            if (methods.length != 0) { //TODO: figure out what this code actually does
                StringBuilder builder = new StringBuilder();
                for (String s : methods) {
                    this.forEachPrimitiveRecursive(primitives1 -> {
                        String in = s;
                        for (int i = primitives.length - 1; i >= 0; i--) {
                            in = primitives[i].format(in, i);
                        }
                        in = in
                                .replaceAll("_method", "_")
                                .replaceAll("<method%", "<%");
                        AtomicReference<String> ref = new AtomicReference<>(in);
                        for (int i = 0; i < primitives1.length; i++) {
                            ref.set(primitives1[i].format(ref.get(), i));
                        }
                        builder.append(ref.get()
                                .replaceAll(GENERIC_HEADER_DEF, Primitive.getGenericHeader(settings, primitives1)));
                    }, depth, settings);
                }
                contentOut = contentOut.replaceAll(METHODS_DEF, builder.toString());
            }

            {
                boolean areAnyGeneric = false;
                if (depth == 0) {
                    areAnyGeneric = true;
                } else {
                    for (int i = primitives.length - 1; i >= 0; i--) {
                        areAnyGeneric |= primitives[i].generic;
                    }
                }
                if (areAnyGeneric) {
                    contentOut = contentOut.replaceAll("\\s*?<!%[\\s\\S]*?%>", "")
                            .replaceAll("<!%[\\s\\S]*?%>", "")
                            .replaceAll("(\\s*?)<%([\\s\\S]*?)%>", "$1$2")
                            .replaceAll("<%([\\s\\S]*?)%>", "$1");
                } else {
                    contentOut = contentOut.replaceAll("\\s*?<%[\\s\\S]*?%>", "")
                            .replaceAll("<%[\\s\\S]*?%>", "")
                            .replaceAll("(\\s*?)<!%([\\s\\S]*?)%>", "$1$2")
                            .replaceAll("<!%([\\s\\S]*?)%>", "$1");
                }
            }
            if (depth == 0) {
                int i = 0;
                for (Primitive p : Primitive.PRIMITIVES) {
                    nameOut = p.format(nameOut, i, settings);
                    contentOut = p.format(contentOut, i++, settings);
                }
            } else {
                for (int i = primitives.length - 1; i >= 0; i--) {
                    Primitive p = primitives[i];
                    nameOut = p.format(nameOut, i, settings);
                    contentOut = p.format(contentOut, i, settings);
                }
            }
            File file = new File(path, nameOut);

            contentOut = contentOut
                    .replaceAll(GENERIC_HEADER_DEF, Primitive.getGenericHeader(settings, primitives))
                    .replaceAll(HEADERS_DEF, imports.isEmpty() ?
                            String.format("%s\n\n%s", LICENSE_DEF, PACKAGE_DEF) :
                            String.format("%s\n\n%s\n\n%s", LICENSE_DEF, PACKAGE_DEF, IMPORTS_DEF))
                    .replaceAll(PACKAGE_DEF, packageName)
                    .replaceAll(IMPORTS_DEF, imports)
                    .replaceAll(LICENSE_DEF, LICENSE);

            try (OutputStream os = new FileOutputStream(file)) {
                byte[] b = contentOut.getBytes(StandardCharsets.UTF_8);
                os.write(b);
                SIZE.addAndGet(file.length());
                FILES.incrementAndGet();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!file.setLastModified(System.currentTimeMillis())) {
                throw new IllegalStateException();
            }
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
                    "net.daporkchop.lib.unsafe.PUnsafe"//,
                    //"lombok.*",
                    //"java.util.*",
                    //"java.util.concurrent.*",
                    //"java.util.concurrent.atomic.*",
                    //"java.util.concurrent.locks.*",
                    //"java.io.*",
                    //"java.nio.*"
            );
            this.importList.addAll(this.additionalImports);
            //this.getImportsRecursive(this.inRoot);
            this.importList.sort(String::compareTo);

            StringBuilder builder = new StringBuilder();
            for (String s : this.importList) {
                builder.append(String.format("import %s;\n", s));
            }
            String s = builder.toString();
            this.imports = s.isEmpty() ? "" : s.substring(0, s.length() - 1);
        }
    }

    private void getImportsRecursive(@NonNull File file) {
        boolean flag = true;
        for (File f : PFiles.ensureDirectoryExists(file).listFiles()) {
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
        File[] files = PFiles.ensureDirectoryExists(file).listFiles();
        if (files == null)  {
            throw new IllegalArgumentException(file.getAbsolutePath() + " has null contents!");
        }
        for (File f : files) {
            if (f.isDirectory() && !f.getName().endsWith(".java")) { //why is isDirectory() returning true when it isn't?
                this.addAllExisting(f);
            } else {
                this.existing.add(String.format("%s.%s", this.getPackageName(file), f.getName()));
            }
        }
    }

    private void forEachPrimitiveRecursive(@NonNull Consumer<Primitive[]> consumer, int depth, @NonNull JsonObject settings, Primitive... primitives) {
        if (depth > primitives.length) {
            Primitive[] p = new Primitive[primitives.length + 1];
            System.arraycopy(primitives, 0, p, 0, primitives.length);
            JsonArray validRoot = settings.has("valid") ? settings.getAsJsonArray("valid") : EMPTY_JSON_ARRAY;
            JsonArray valid = validRoot.size() >= p.length ? validRoot.get(p.length - 1).getAsJsonArray() : EMPTY_JSON_ARRAY;
            Primitive.PRIMITIVES.forEach(primitive -> {
                if (valid.size() != 0) {
                    //only if not empty
                    String primitiveFullName = primitive.fullName;
                    boolean flag = false;
                    for (JsonElement element : valid) {
                        if (element.getAsString().equalsIgnoreCase(primitiveFullName)) {
                            flag = true;
                        }
                    }
                    if (!flag) {
                        return;
                    }
                }
                p[p.length - 1] = primitive;
                this.forEachPrimitiveRecursive(consumer, depth, settings, p);
            });
        } else {
            consumer.accept(primitives);
        }
    }
}
