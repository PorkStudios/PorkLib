/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2021 DaPorkchop_
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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.oio.StreamUtil;
import net.daporkchop.lib.common.function.PFunctions;
import net.daporkchop.lib.common.misc.InstancePool;
import net.daporkchop.lib.common.misc.Tuple;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.misc.string.PStrings;
import net.daporkchop.lib.common.reference.ReferenceStrength;
import net.daporkchop.lib.common.reference.cache.Cached;
import net.daporkchop.lib.primitive.generator.option.HeaderOptions;
import net.daporkchop.lib.primitive.generator.option.Parameter;
import net.daporkchop.lib.primitive.generator.option.ParameterContext;
import net.daporkchop.lib.primitive.generator.replacer.ComplexGenericReplacer;
import net.daporkchop.lib.primitive.generator.replacer.FileHeaderReplacer;
import net.daporkchop.lib.primitive.generator.replacer.GenericHeaderReplacer;

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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static net.daporkchop.lib.common.util.PValidation.*;

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
            , new Generator(
                    new File("src/main/resources/test/java"),
                    new File("../src/test/java"),
                    new File("src/main/resources/test/test.json"))
            , new Generator(
                    new File("src/main/resources/lambda/java"),
                    new File("../lambda/src/generated/java"),
                    new File("src/main/resources/lambda/lambda.json"))
    ).filter(Objects::nonNull).collect(Collectors.toList());

    public static final String LICENSE;
    public static final OverrideReplacer OVERRIDES;

    private static final Cached<StringBuffer> STRINGBUFFER_CACHE = Cached.threadLocal(StringBuffer::new, ReferenceStrength.WEAK);
    private static final Cached<StringBuffer> STRINGBUFFER_CACHE_2 = Cached.threadLocal(StringBuffer::new, ReferenceStrength.WEAK);
    private static final Cached<Matcher> NAME_MATCHER_CACHE = Cached.regex(Pattern.compile("_P(\\d+)_"));
    private static final Cached<Matcher> TOKEN_MATCHER_CACHE = Cached.regex(Pattern.compile("_([a-zA-Z0-9]*?)(?:([pP])(\\d+))?_"));

    private static final Pattern GENERIC_FILTER_PATTERN = Pattern.compile("<(\\d+)?(!)?%((?:.*?(?:<\\d+?!?%.*?%>)?)*)%>", Pattern.DOTALL);
    private static final Cached<Matcher> GENERIC_FILTER_CACHE = Cached.regex(GENERIC_FILTER_PATTERN, true);
    private static final Cached<Matcher> GENERIC_FILTER_CACHE_2 = Cached.regex(GENERIC_FILTER_PATTERN, true);

    private static final Pattern TYPE_FILTER_PATTERN = Pattern.compile("<((?:\\d+[a-zA-Z]+)+)(!)?%((?:.*?(?:<(?:\\d+[a-zA-Z]+)+!?%.*?%>)?)*)%>", Pattern.DOTALL);
    private static final Cached<Matcher> TYPE_FILTER_CACHE = Cached.regex(TYPE_FILTER_PATTERN, true);
    private static final Cached<Matcher> TYPE_FILTER_CACHE_2 = Cached.regex(TYPE_FILTER_PATTERN, true);
    private static final Pattern TYPE_FILTER_EXTRACT_PATTERN = Pattern.compile("(\\d+)([a-zA-Z]+)");
    private static final Cached<Matcher> TYPE_FILTER_EXTRACT_CACHE = Cached.regex(TYPE_FILTER_EXTRACT_PATTERN, true);
    private static final Cached<Matcher> TYPE_FILTER_EXTRACT_CACHE_2 = Cached.regex(TYPE_FILTER_EXTRACT_PATTERN, true);

    static {
        try {
            try (InputStream is = new FileInputStream(new File("../../LICENSE"))) {
                LICENSE = String.format("/*\n * %s\n */",
                        new String(StreamUtil.toByteArray(is), StandardCharsets.UTF_8)
                                .replace("$today.year", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)))
                                .replaceAll("\n", "\n * "));
            }

            try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("src/main/resources/global.json")), StandardCharsets.UTF_8))) {
                JsonObject global = InstancePool.getInstance(JsonParser.class).parse(reader).getAsJsonObject();
                OVERRIDES = new OverrideReplacer(global.getAsJsonObject("overrides"));
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

        System.out.println(Stream.of(
                new Tuple<>(NAME_TIME.get(), "Name"),
                new Tuple<>(NAME_OVERRIDE_TIME.get(), "Name overrides"),
                new Tuple<>(GENERIC_FILTER_TIME.get(), "Filter generics"),
                new Tuple<>(TYPE_FILTER_TIME.get(), "Filter types"),
                new Tuple<>(TOKEN_REPLACE_TIME.get(), "Replace tokens"),
                new Tuple<>(CONTENT_OVERRIDE_TIME.get(), "Content overrides"),
                new Tuple<>(UTF8_ENCODE_TIME.get(), "UTF8 encode"))
                .sorted(Comparator.comparingLong(Tuple::getA))
                .map(t -> String.format("%.2fms, %s", t.getA() / 1000000.0d, t.getB()))
                .collect(Collectors.joining("\n")));
    }

    public static final AtomicLong NAME_TIME = new AtomicLong();
    public static final AtomicLong NAME_OVERRIDE_TIME = new AtomicLong();
    public static final AtomicLong GENERIC_FILTER_TIME = new AtomicLong();
    public static final AtomicLong TYPE_FILTER_TIME = new AtomicLong();
    public static final AtomicLong TOKEN_REPLACE_TIME = new AtomicLong();
    public static final AtomicLong CONTENT_OVERRIDE_TIME = new AtomicLong();
    public static final AtomicLong UTF8_ENCODE_TIME = new AtomicLong();

    public final File inRoot;
    public final File outRoot;
    private final Collection<File> existing = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Collection<File> generated = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final String imports;
    private final TokenReplacer[] tokenReplacers;

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

        this.tokenReplacers = new TokenReplacer[]{
                new GenericHeaderReplacer(),
                new FileHeaderReplacer(this.imports),
                new ComplexGenericReplacer()
        };
    }

    public void generate() {
        this.addAllExisting(PFiles.ensureDirectoryExists(this.outRoot));

        this.generate(this.inRoot, this.outRoot);

        System.out.printf("Existing: %d, generated: %d\n", this.existing.size(), this.generated.size());
        AtomicLong deletedFiles = new AtomicLong(0L);
        AtomicLong deletedSize = new AtomicLong(0L);
        this.existing.parallelStream()
                .filter(PFunctions.not(this.generated::contains))
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
            String name = file.getName().substring(0, file.getName().length() - ".template".length());
            String packageName = this.getPackageName(file);

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
            stream/*.parallel()*/.forEach(params -> this.generate0(out, name, content, pkg, options, params));
        }
    }

    private void generate0(@NonNull File dir, @NonNull String name, @NonNull String content, @NonNull String pkg, @NonNull HeaderOptions options, @NonNull List<ParameterContext> params) {
        StringBuffer buffer = STRINGBUFFER_CACHE.get();
        Matcher matcher;

        long time;
        time = System.nanoTime();
        matcher = NAME_MATCHER_CACHE.get().reset(name);
        if (matcher.find()) {
            buffer.setLength(0);
            do {
                matcher.appendReplacement(buffer, params.get(Integer.parseUnsignedInt(matcher.group(1))).primitive().displayName);
            } while (matcher.find());
            matcher.appendTail(buffer);
            name = buffer.toString();
        }

        NAME_TIME.getAndAdd(System.nanoTime() - time);
        time = System.nanoTime();

        if ((name = OVERRIDES.processName(name, -1, buffer)) == null) {
            return;
        }
        name += ".java";

        NAME_OVERRIDE_TIME.getAndAdd(System.nanoTime() - time);

        File file = new File(dir, name);
        checkState(this.generated.add(file), "File %s was already generated?!?", name);
        if (file.lastModified() == options.lastModified()) {
            return;
        }

        String contentOut = content;
        time = System.nanoTime();

        //start off by filtering generic things out
        matcher = GENERIC_FILTER_CACHE.get().reset(contentOut);
        boolean anyGeneric = params.stream().map(ParameterContext::primitive).anyMatch(Primitive::isGeneric);
        if (matcher.find()) {
            buffer.setLength(0);
            do {
                String numberTxt = matcher.group(1);
                boolean generic = numberTxt == null ? anyGeneric : params.get(Integer.parseUnsignedInt(numberTxt)).primitive().generic;
                boolean inverted = matcher.group(2) != null;
                if (generic ^ inverted) {
                    String content2 = matcher.group(3);
                    Matcher matcher2 = GENERIC_FILTER_CACHE_2.get().reset(content2);
                    if (matcher2.find()) {
                        StringBuffer buffer2 = STRINGBUFFER_CACHE_2.get();
                        buffer2.setLength(0);
                        do {
                            String numberTxt2 = matcher2.group(1);
                            boolean generic2 = numberTxt2 == null ? anyGeneric : params.get(Integer.parseUnsignedInt(numberTxt2)).primitive().generic;
                            boolean inverted2 = matcher2.group(2) != null;
                            matcher2.appendReplacement(buffer2, generic2 ^ inverted2 ? matcher2.group(3) : "");
                        } while (matcher2.find());
                        matcher2.appendTail(buffer2);
                        content2 = buffer2.toString();
                    }
                    matcher.appendReplacement(buffer, content2);
                } else {
                    matcher.appendReplacement(buffer, "");
                }
            } while (matcher.find());
            matcher.appendTail(buffer);
            contentOut = buffer.toString();
        }

        GENERIC_FILTER_TIME.getAndAdd(System.nanoTime() - time);
        time = System.nanoTime();

        matcher = TYPE_FILTER_CACHE.get().reset(contentOut);
        if (matcher.find()) {
            buffer.setLength(0);
            do {
                boolean invert = matcher.group(2) != null;
                boolean valid = false;
                Matcher extractMatcher = TYPE_FILTER_EXTRACT_CACHE.get().reset(matcher.group(1));
                while (extractMatcher.find()) {
                    int index = Integer.parseUnsignedInt(extractMatcher.group(1));
                    Primitive primitive = Primitive.BY_NAME.get(extractMatcher.group(2));
                    valid |= params.get(index).primitive().equals(primitive);
                }
                if (valid ^ invert) {
                    String content2 = matcher.group(3);
                    Matcher matcher2 = TYPE_FILTER_CACHE_2.get().reset(content2);
                    if (matcher2.find()) {
                        StringBuffer buffer2 = STRINGBUFFER_CACHE_2.get();
                        buffer2.setLength(0);
                        do {
                            boolean inverted2 = matcher2.group(2) != null;
                            boolean valid2 = false;
                            extractMatcher.reset(matcher2.group(1));
                            while (extractMatcher.find()) {
                                int index = Integer.parseUnsignedInt(extractMatcher.group(1));
                                Primitive primitive = Primitive.BY_NAME.get(extractMatcher.group(2));
                                valid2 |= params.get(index).primitive().equals(primitive);
                            }
                            matcher2.appendReplacement(buffer2, valid2 ^ inverted2 ? matcher2.group(3) : "");
                        } while (matcher2.find());
                        matcher2.appendTail(buffer2);
                        content2 = buffer2.toString();
                    }
                    matcher.appendReplacement(buffer, content2);
                } else {
                    matcher.appendReplacement(buffer, "");
                }
            } while (matcher.find());
            matcher.appendTail(buffer);
            contentOut = buffer.toString();
        }

        TYPE_FILTER_TIME.getAndAdd(System.nanoTime() - time);
        time = System.nanoTime();

        matcher = TOKEN_MATCHER_CACHE.get().reset(contentOut);
        if (matcher.find()) {
            buffer.setLength(0);
            MAIN:
            do {
                String original = matcher.group();
                String numberTxt = matcher.group(3);
                if (numberTxt != null) {
                    String token = matcher.group(1);
                    boolean lowerCase = matcher.group(2).charAt(0) == 'p';
                    String text = params.get(Integer.parseUnsignedInt(numberTxt)).replace(token, lowerCase, params);
                    if (text != null) {
                        matcher.appendReplacement(buffer, text);
                        continue;
                    }
                } else {
                    for (TokenReplacer replacer : this.tokenReplacers) {
                        String text = replacer.replace(original, params, pkg);
                        if (text != null) {
                            matcher.appendReplacement(buffer, text);
                            continue MAIN;
                        }
                    }
                }
                matcher.appendReplacement(buffer, original);
            } while (matcher.find());
            matcher.appendTail(buffer);
            contentOut = buffer.toString();
        }

        TOKEN_REPLACE_TIME.getAndAdd(System.nanoTime() - time);
        time = System.nanoTime();

        if ((contentOut = OVERRIDES.processCode(contentOut, -1, buffer)) == null) {
            throw new IllegalStateException();
        }

        CONTENT_OVERRIDE_TIME.getAndAdd(System.nanoTime() - time);
        time = System.nanoTime();

        byte[] b = contentOut.getBytes(StandardCharsets.UTF_8);
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        checkState(file.setLastModified(options.lastModified()));

        UTF8_ENCODE_TIME.getAndAdd(System.nanoTime() - time);

        SIZE.addAndGet(b.length);
        FILES.incrementAndGet();
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
            } else if (f.isFile()) {
                this.existing.add(f);
            }
        }
    }
}
