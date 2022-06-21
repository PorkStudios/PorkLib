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

package net.daporkchop.lib.primitive.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.lib.common.function.io.IOFunction;
import net.daporkchop.lib.common.misc.Tuple;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.reference.ReferenceStrength;
import net.daporkchop.lib.common.reference.cache.Cached;
import net.daporkchop.lib.primitive.generator.config.GeneratorConfig;
import net.daporkchop.lib.primitive.generator.option.HeaderOptions;
import net.daporkchop.lib.primitive.generator.option.Parameter;
import net.daporkchop.lib.primitive.generator.option.ParameterContext;
import net.daporkchop.lib.primitive.generator.replacer.ComplexGenericReplacer;
import net.daporkchop.lib.primitive.generator.replacer.FileHeaderReplacer;
import net.daporkchop.lib.primitive.generator.replacer.GenericHeaderReplacer;
import net.daporkchop.lib.primitive.generator.util.ignore.IgnoreProcessor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class Generator implements Runnable {
    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);

    static {
        NUMBER_FORMAT.setMaximumFractionDigits(2);
    }

    public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

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

    public static String replace(@NonNull Matcher matcher, @NonNull CharSequence original, @NonNull Function<? super Matcher, String> replacer) {
        StringBuffer buffer = new StringBuffer();

        matcher.reset(original);
        while (matcher.find()) {
            matcher.appendReplacement(buffer, replacer.apply(matcher));
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }

    public static void main(String... args) {
        if (args.length != 3) {
            System.err.println("usage:\n    Generator <license file> <input directory> <output directory>");
            System.exit(1);
        }

        Path inRoot = Paths.get(args[1]);
        Path outRoot = Paths.get(args[2]);

        new Generator(inRoot, outRoot).run();
    }

    public final Path inRoot;
    public final Path outRoot;

    private final Map<Path, FileTime> existingFiles = new ConcurrentHashMap<>();
    private final Collection<Path> generated = ConcurrentHashMap.newKeySet();

    private final TokenReplacer[] tokenReplacers;

    public final LongAdder generatedFiles = new LongAdder();
    public final LongAdder generatedSize = new LongAdder();

    public final LongAdder timeName = new LongAdder();
    public final LongAdder timeNameOverride = new LongAdder();
    public final LongAdder timeFilterGeneric = new LongAdder();
    public final LongAdder timeFilterType = new LongAdder();
    public final LongAdder timeReplaceToken = new LongAdder();
    public final LongAdder timeContentOverride = new LongAdder();
    public final LongAdder timeWrite = new LongAdder();

    public Generator(@NonNull Path inRoot, @NonNull Path outRoot) {
        this.inRoot = PFiles.assertDirectoryExists(inRoot);
        this.outRoot = PFiles.ensureDirectoryExists(outRoot);

        this.tokenReplacers = new TokenReplacer[]{
                new GenericHeaderReplacer(),
                new FileHeaderReplacer(),
                new ComplexGenericReplacer()
        };
    }

    @Override
    public void run() {
        this.indexExistingFiles(this.existingFiles, this.outRoot, IgnoreProcessor.begin(this.outRoot));
        System.out.println("Existing source files: " + NUMBER_FORMAT.format(this.existingFiles.size()));

        this.generateDirectory(GeneratorConfig.DEFAULT, this.getLatestClassModificationFileTime(), this.inRoot, this.outRoot);
        System.out.println("Generated " + NUMBER_FORMAT.format(this.generatedFiles.sum()) + " files, totalling " + NUMBER_FORMAT.format(this.generatedSize.sum()) + " bytes ("
                           + NUMBER_FORMAT.format(this.generatedSize.sum() / (1024.0d * 1024.0d)) + " MiB)");

        LongAdder deletedFiles = new LongAdder();
        LongAdder deletedSize = new LongAdder();
        this.existingFiles.keySet().parallelStream().forEach((IOConsumer<Path>) file -> {
            if (!this.generated.contains(file)) {
                deletedFiles.increment();
                deletedSize.add(Files.size(file));

                PFiles.rm(file);
            }
        });

        System.out.println("Deleted " + NUMBER_FORMAT.format(deletedFiles.sum()) + " old files, totalling " + NUMBER_FORMAT.format(deletedSize.sum()) + " bytes ("
                           + NUMBER_FORMAT.format(deletedSize.sum() / (1024.0d * 1024.0d)) + " MiB)");

        System.out.println(Stream.of(
                new Tuple<>(this.timeName.sum(), "  Name"),
                new Tuple<>(this.timeNameOverride.sum(), "  Name overrides"),
                new Tuple<>(this.timeFilterGeneric.sum(), "  Filter generics"),
                new Tuple<>(this.timeFilterType.sum(), "  Filter types"),
                new Tuple<>(this.timeReplaceToken.sum(), "  Replace tokens"),
                new Tuple<>(this.timeContentOverride.sum(), "  Content overrides"),
                new Tuple<>(this.timeWrite.sum(), "  UTF-8 encode & write"))
                .sorted(Comparator.comparingLong(Tuple::getA))
                .map(t -> "  " + NUMBER_FORMAT.format(t.getA() / 1000000.0d) + " @ " + t.getB())
                .collect(Collectors.joining("\n", "\nPerformance analysis:\n", "")));
    }

    @SneakyThrows({ IOException.class, URISyntaxException.class })
    private FileTime getLatestClassModificationFileTime() {
        try (Stream<Path> stream = Files.walk(Paths.get(Generator.class.getProtectionDomain().getCodeSource().getLocation().toURI()))) {
            return stream.filter(Files::isRegularFile)
                    .map((IOFunction<Path, FileTime>) Files::getLastModifiedTime)
                    .max(Comparator.naturalOrder())
                    .get();
        }
    }

    @SneakyThrows(IOException.class)
    public void generateDirectory(@NonNull GeneratorConfig config, @NonNull FileTime latestExpectedTime, @NonNull Path inDirectory, @NonNull Path outDirectory) {
        assert Files.isDirectory(inDirectory) : "directory doesn't exist: " + inDirectory;

        List<ForkJoinTask<?>> childTasks = new ArrayList<>();

        //if present, load config.json and merge with existing config
        GeneratorConfig childConfig;
        FileTime nextExpectedTime;

        Path childConfigPath = inDirectory.resolve("config.json");
        if (PFiles.checkFileExists(childConfigPath)) {
            childConfig = config.mergeConfiguration(new JsonParser().parse(new String(Files.readAllBytes(childConfigPath), StandardCharsets.UTF_8)).getAsJsonObject());

            FileTime configAffectedExpectedTime = childConfig.potentiallyAffectedByFiles()
                    .filter(PFiles::checkFileExists)
                    .map((IOFunction<Path, FileTime>) Files::getLastModifiedTime)
                    .max(Comparator.naturalOrder())
                    .orElse(latestExpectedTime);
            nextExpectedTime = latestExpectedTime.compareTo(configAffectedExpectedTime) >= 0 ? latestExpectedTime : configAffectedExpectedTime;
        } else {
            childConfig = config;
            nextExpectedTime = latestExpectedTime;
        }

        //list the files under the input directory and schedule sub-futures for them
        Files.walkFileTree(inDirectory, Collections.emptySet(), 1, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile()) {
                    if (file.getFileName().toString().endsWith(".template")) { //schedule task to asynchronously generate the template
                        childTasks.add(ForkJoinTask.adapt(() -> Generator.this.generateFile(childConfig, nextExpectedTime, file, attrs, outDirectory)));
                    }
                } else if (attrs.isDirectory()) {
                    if (!file.getFileName().toString().endsWith("_methods")) { //schedule task to asynchronously recurse into the subdirectory
                        childTasks.add(ForkJoinTask.adapt(() -> Generator.this.generateDirectory(childConfig, nextExpectedTime, file, outDirectory.resolve(file.getFileName()))));
                    }
                } else {
                    throw new IllegalArgumentException("don't know how to handle: " + file);
                }

                return super.visitFile(file, attrs);
            }
        });

        //execute all the sub-tasks and wait for their completion
        ForkJoinTask.invokeAll(childTasks);
    }

    @SneakyThrows(IOException.class)
    private void generateFile(@NonNull GeneratorConfig config, @NonNull FileTime latestExpectedTime, @NonNull Path templateFile, @NonNull BasicFileAttributes templateFileAttrs, @NonNull Path outDirectory) {
        String name = templateFile.getFileName().toString().substring(0, templateFile.getFileName().toString().length() - ".template".length());
        Path configFile = PFiles.assertFileExists(templateFile.resolveSibling(name + ".json"));

        String packageName = "package " + this.getPackageName(templateFile) + ';';

        FileTime expectedResultTime = Stream.of(latestExpectedTime, templateFileAttrs.lastModifiedTime(), Files.getLastModifiedTime(configFile))
                .max(Comparator.naturalOrder()).get();

        HeaderOptions headerOptions = new HeaderOptions(new JsonParser().parse(new String(Files.readAllBytes(configFile), StandardCharsets.UTF_8)).getAsJsonObject());
        String content = new String(Files.readAllBytes(templateFile), StandardCharsets.UTF_8);

        PFiles.ensureDirectoryExists(outDirectory);

        Stream<List<ParameterContext>> stream = Stream.of(Collections.emptyList());
        for (Parameter parameter : headerOptions.parameters()) {
            stream = stream.flatMap(currentParams -> parameter.primitives().stream()
                    .map(primitive -> new ParameterContext(parameter, primitive))
                    .map(ctx -> {
                        List<ParameterContext> params = new ArrayList<>(currentParams.size() + 1);
                        params.addAll(currentParams);
                        params.add(ctx);
                        return params;
                    }));
        }

        //spawn a sub-task for each combination of parameters, then execute them all and wait for them all to complete
        ForkJoinTask.invokeAll(stream
                .map(params -> ForkJoinTask.adapt(() -> this.generate0(outDirectory, name, expectedResultTime, content, packageName, config, headerOptions, params)))
                .collect(Collectors.toList()));
    }

    @SneakyThrows(IOException.class)
    private void generate0(@NonNull Path outDirectory, @NonNull String templateFileName, @NonNull FileTime expectedResultTime, @NonNull String templateFileContent, @NonNull String packageName, @NonNull GeneratorConfig config, @NonNull HeaderOptions headerOptions, @NonNull List<ParameterContext> params) {
        String name;
        { //name
            long time = System.nanoTime(); //begin profiling timeName

            Matcher matcher = NAME_MATCHER_CACHE.get().reset(templateFileName);
            if (matcher.find()) {
                StringBuffer buffer = STRINGBUFFER_CACHE.get();
                buffer.setLength(0);
                do {
                    matcher.appendReplacement(buffer, params.get(Integer.parseUnsignedInt(matcher.group(1))).primitive().displayName);
                } while (matcher.find());
                matcher.appendTail(buffer);
                name = buffer.toString();
            } else {
                name = templateFileName;
            }

            this.timeName.add(System.nanoTime() - time);
        }

        { //name override
            long time = System.nanoTime();
            try {
                if ((name = config.getOverrideReplacer().processName(name, -1, STRINGBUFFER_CACHE.get())) == null) {
                    //the template file's name is supposed to be replaced!
                    return;
                }
                name += ".java";
            } finally {
                this.timeNameOverride.add(System.nanoTime() - time);
            }
        }

        Path file = outDirectory.resolve(name);
        checkState(this.generated.add(file), "File %s was already generated?!?", name);

        if (PFiles.checkFileExists(file) && Files.getLastModifiedTime(file).toMillis() == expectedResultTime.toMillis()) {
            return;
        }

        String contentOut = templateFileContent;
        contentOut = this.processGenericFilters(contentOut, params);
        contentOut = this.processTypeFilters(contentOut, params);
        contentOut = this.processTokens(contentOut, params, packageName, config);

        { //overrides
            long time = System.nanoTime();

            if ((contentOut = config.getOverrideReplacer().processCode(contentOut, -1, STRINGBUFFER_CACHE.get())) == null) {
                throw new IllegalStateException();
            }

            this.timeContentOverride.add(System.nanoTime() - time);
        }

        { //write
            long time = System.nanoTime();

            byte[] b = contentOut.getBytes(StandardCharsets.UTF_8);
            if (!PFiles.checkFileExists(file) || !Arrays.equals(Files.readAllBytes(file), b)) { //only (over)write file contents if the file doesn't exist or its contents are different
                Files.write(file, b);
            }
            Files.setLastModifiedTime(file, expectedResultTime);

            this.generatedSize.add(b.length);
            this.generatedFiles.increment();

            this.timeWrite.add(System.nanoTime() - time);
        }
    }

    private String processGenericFilters(@NonNull String text, @NonNull List<ParameterContext> params) {
        long time = System.nanoTime();

        Matcher matcher = GENERIC_FILTER_CACHE.get().reset(text);
        boolean anyGeneric = params.stream().map(ParameterContext::primitive).anyMatch(Primitive::isGeneric);
        if (matcher.find()) {
            StringBuffer buffer = STRINGBUFFER_CACHE.get();
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

            text = buffer.toString();
        }

        this.timeFilterGeneric.add(System.nanoTime() - time);
        return text;
    }

    private String processTypeFilters(@NonNull String text, @NonNull List<ParameterContext> params) {
        long time = System.nanoTime();

        Matcher matcher = TYPE_FILTER_CACHE.get().reset(text);
        if (matcher.find()) {
            StringBuffer buffer = STRINGBUFFER_CACHE.get();
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
            text = buffer.toString();
        }

        this.timeFilterType.add(System.nanoTime() - time);
        return text;
    }

    private String processTokens(@NonNull String text, @NonNull List<ParameterContext> params, @NonNull String packageName, @NonNull GeneratorConfig config) {
        long time = System.nanoTime();

        Matcher matcher = TOKEN_MATCHER_CACHE.get().reset(text);
        if (matcher.find()) {
            StringBuffer buffer = STRINGBUFFER_CACHE.get();
            buffer.setLength(0);
            MAIN:
            do {
                String original = matcher.group();
                String numberTxt = matcher.group(3);
                if (numberTxt != null) {
                    String token = matcher.group(1);
                    boolean lowerCase = matcher.group(2).charAt(0) == 'p';
                    String value = params.get(Integer.parseUnsignedInt(numberTxt)).replace(token, lowerCase, params);
                    if (value != null) {
                        matcher.appendReplacement(buffer, value);
                        continue;
                    }
                } else {
                    for (TokenReplacer replacer : this.tokenReplacers) {
                        String value = replacer.replace(config, original, params, packageName);
                        if (value != null) {
                            matcher.appendReplacement(buffer, value);
                            continue MAIN;
                        }
                    }
                }
                matcher.appendReplacement(buffer, original);
            } while (matcher.find());
            matcher.appendTail(buffer);
            text = buffer.toString();
        }

        this.timeReplaceToken.add(System.nanoTime() - time);
        return text;
    }

    private String getPackageName(@NonNull Path file) {
        if (!Files.isDirectory(file)) {
            file = file.getParent();
        }

        //temporarily buffer in a Deque to reverse all names
        Deque<String> deque = new ArrayDeque<>();
        String fileName;
        for (; !"java".equals(fileName = file.getFileName().toString()); file = file.getParent()) {
            deque.addFirst(fileName);
        }

        StringJoiner joiner = new StringJoiner(".");
        deque.forEach(joiner::add);
        return joiner.toString();
    }

    @SneakyThrows(IOException.class)
    private void indexExistingFiles(@NonNull Map<Path, FileTime> dst, @NonNull Path dir, @NonNull IgnoreProcessor ignoreProcessor) {
        List<ForkJoinTask<?>> tasks = new ArrayList<>();

        Files.walkFileTree(dir, Collections.emptySet(), 1, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (ignoreProcessor.shouldIgnore(file, attrs)) {
                    System.out.println("ignoring " + file);
                } else if (attrs.isRegularFile()) {
                    if (file.getFileName().toString().endsWith(".java")) { //we only care about java files
                        checkState(dst.putIfAbsent(file, attrs.lastModifiedTime()) == null, "file %s is already indexed?!?", file);
                    }
                } else if (attrs.isDirectory()) { //schedule a task which will asynchronously recurse into the subdirectory
                    tasks.add(ForkJoinTask.adapt(() -> Generator.this.indexExistingFiles(dst, file, ignoreProcessor.enterDirectory(file))));
                } else {
                    throw new IllegalArgumentException("don't know what to do with file: " + file);
                }

                return super.visitFile(file, attrs);
            }
        });

        //asynchronously recurse into all of the subtrees
        ForkJoinTask.invokeAll(tasks);
    }
}
