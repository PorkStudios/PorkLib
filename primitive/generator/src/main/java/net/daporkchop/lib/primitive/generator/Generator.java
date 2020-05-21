/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
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

package net.daporkchop.lib.primitive.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.oio.StreamUtil;
import net.daporkchop.lib.common.misc.InstancePool;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.misc.string.PStrings;
import net.daporkchop.lib.primitive.generator.option.HeaderOptions;
import net.daporkchop.lib.primitive.generator.option.Parameter;
import net.daporkchop.lib.primitive.generator.option.ParameterContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.primitive.generator.Primitive.*;

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
                    new File("src/main/resources/primitive/primitive.json"))
            /*, new Generator(
                    new File("src/main/resources/test/java"),
                    new File("../src/test/java"),
                    Collections.singletonList("org.junit.*"))*/
            , new Generator(
                    new File("src/main/resources/lambda/java"),
                    new File("../lambda/src/generated/java"),
                    new File("src/main/resources/lambda/lambda.json"))
    ).filter(Objects::nonNull).collect(Collectors.toList());
    public static final String LICENSE;
    public static final Collection<OverrideReplacer> OVERRIDES;
    private static final JsonArray EMPTY_JSON_ARRAY = new JsonArray();

    static {
        try {
            try (InputStream is = new FileInputStream(new File("../../LICENSE"))) {
                LICENSE = String.format("/*\n * %s\n */",
                        new String(StreamUtil.toByteArray(is), StandardCharsets.UTF_8)
                                .replace("$today.year", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)))
                                .replaceAll("\n", "\n * "));
            }

            try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("src/main/resources/overrides.json")), StandardCharsets.UTF_8))) {
                OVERRIDES = Collections.unmodifiableList(StreamSupport.stream(
                        InstancePool.getInstance(JsonParser.class).parse(reader).getAsJsonArray().spliterator(), false)
                        .map(JsonElement::getAsJsonObject)
                        .map(OverrideReplacer::new)
                        .collect(Collectors.toList()));
            }
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public static void main(String... args) throws IOException {
        for (Generator generator : GENERATORS) {
            generator.generate();
        }

        System.out.printf(
                "Generated %d files, totalling %s bytes (%.2f megabytes)\n",
                FILES.get(),
                NumberFormat.getInstance(Locale.US).format(SIZE.get()),
                (double) SIZE.get() / 1024.0d / 1024.0d);
    }

    public final File inRoot;
    public final File outRoot;
    private final Collection<String> existing = new HashSet<>();
    private final Collection<String> generated = new HashSet<>();
    private final String imports;

    public Generator(@NonNull File inRoot, @NonNull File outRoot, @NonNull File manifestFile) {
        this.inRoot = PFiles.assertDirectoryExists(inRoot);
        this.outRoot = PFiles.ensureDirectoryExists(outRoot);

        JsonObject manifest;
        try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(PFiles.assertFileExists(manifestFile)), StandardCharsets.UTF_8))) {
            manifest = InstancePool.getInstance(JsonParser.class).parse(reader).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.imports = StreamSupport.stream(manifest.getAsJsonArray("imports").spliterator(), false)
                .map(JsonElement::getAsString)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .map(s -> PStrings.fastFormat("import %s;", s))
                .collect(Collectors.joining("\n"));
    }

    public void generate() {
        this.addAllExisting(PFiles.ensureDirectoryExists(this.outRoot));

        this.generate(this.inRoot, this.outRoot);

        this.existing.removeAll(this.generated);
        System.out.printf("Existing: %d, generated: %d\n", this.existing.size(), this.generated.size());
        AtomicLong deletedFiles = new AtomicLong(0L);
        AtomicLong deletedSize = new AtomicLong(0L);
        this.existing.parallelStream()
                .map(name -> {
                    int dotIndex = name.substring(0, name.length() - ".java".length()).lastIndexOf('.');
                    return new File(new File(this.outRoot, name.substring(0, dotIndex).replace('.', '/')), name.substring(dotIndex + 1, name.length()));
                })
                .filter(file -> !this.generated.contains(file.getName()))
                .forEach(file -> {
                    deletedSize.addAndGet(file.length());
                    PFiles.rm(file);
                    deletedFiles.incrementAndGet();
                });

        System.out.printf(
                "Deleted %d old files, totalling %s bytes (%.2f megabytes)\n",
                deletedFiles.get(),
                NumberFormat.getInstance(Locale.US).format(deletedSize.get()),
                deletedSize.get() / 1024.0d / 1024.0d);
    }

    public void generate(@NonNull File file, @NonNull File out) {
        if (!file.exists()) {
            throw new IllegalStateException();
        }
        if (file.isDirectory()) {
            if (file.getName().endsWith("_methods")) {
                return;
            }
            File realOut = "java".equals(file.getName()) ? out : new File(out, file.getName());
            for (File f : file.listFiles()) {
                this.generate(f, realOut);
            }
        } else if (file.getName().endsWith(".template")) {
            String name = file.getName();
            String packageName = this.getPackageName(file);
            this.generated.add(String.format("%s.%s", packageName, name));

            String rawContent;
            try (InputStream is = new FileInputStream(file)) {
                rawContent = new String(StreamUtil.toByteArray(is), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String content;
            HeaderOptions options;
            {
                JsonObject obj;
                if (rawContent.startsWith("$$$settings$$$")) {
                    String[] split = rawContent.split("_headers_", 2);
                    content = "_headers_" + split[1];
                    try (Reader reader = new StringReader(split[0])) {
                        reader.skip("$$$settings$$$".length());
                        obj = InstancePool.getInstance(JsonParser.class).parse(reader).getAsJsonObject();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    content = rawContent;
                    obj = new JsonObject();
                }
                options = new HeaderOptions(obj, file);
            }

            System.out.printf("Generating %s\n", name);
            PFiles.ensureDirectoryExists(out);
            Stream<List<ParameterContext>> stream = Stream.of(Collections.emptyList());
            for (Parameter parameter : options.parameters()) {
                stream = stream.flatMap(currentParams -> parameter.primitives().stream()
                        .map(primitive -> new ParameterContext(parameter, primitive))
                        .map(ctx -> {
                            List<ParameterContext> params = new ArrayList<>(currentParams.size() + 1);
                            params.addAll(currentParams);
                            params.add(ctx);
                            return params;
                        }));
            }

            String pkg = String.format("package %s;", packageName);
            stream.parallel().forEach(params -> this.generate0(out, name, content, pkg, options, params));
        }
    }

    private void generate0(@NonNull File dir, @NonNull String name, @NonNull String content, @NonNull String pkg, @NonNull HeaderOptions options, @NonNull List<ParameterContext> params) {
        String nameOut = name.replace(".template", ".java");
        for (ParameterContext ctx : params) {
            nameOut = ctx.primitive().format(nameOut, ctx.parameter().index(), params);
        }
        for (OverrideReplacer replacer : OVERRIDES) {
            if ((nameOut = replacer.processName(nameOut, -1)) == null) {
                return;
            }
        }

        checkState(this.generated.add(nameOut), "File %s was already generated?!?", name);
        File file = new File(dir, nameOut);
        if (file.lastModified() == options.lastModified()) {
            return;
        }

        String contentOut = content;

        if (params.stream().map(ParameterContext::primitive).anyMatch(Primitive::isGeneric)) {
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

        for (ParameterContext ctx : params) {
            nameOut = ctx.primitive().format(nameOut, ctx.parameter().index(), params);
            contentOut = ctx.primitive().format(contentOut, ctx.parameter().index(), params);
        }

        contentOut = contentOut
                .replaceAll(GENERIC_HEADER_DEF, Primitive.getGenericHeader(params))
                .replaceAll(HEADERS_DEF, imports.isEmpty() ?
                                         String.format("%s\n\n%s", LICENSE_DEF, PACKAGE_DEF) :
                                         String.format("%s\n\n%s\n\n%s", LICENSE_DEF, PACKAGE_DEF, IMPORTS_DEF))
                .replaceAll(PACKAGE_DEF, pkg)
                .replaceAll(IMPORTS_DEF, imports)
                .replaceAll(LICENSE_DEF, LICENSE);

        for (OverrideReplacer replacer : OVERRIDES) {
            if ((contentOut = replacer.processCode(contentOut, -1)) == null) {
                return;
            }
        }

        try (OutputStream os = new FileOutputStream(file)) {
            byte[] b = contentOut.getBytes(StandardCharsets.UTF_8);
            os.write(b);
            SIZE.addAndGet(b.length);
            FILES.incrementAndGet();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        checkState(file.setLastModified(options.lastModified()));
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

    private void addAllExisting(@NonNull File file) {
        File[] files = PFiles.ensureDirectoryExists(file).listFiles();
        if (files == null) {
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
