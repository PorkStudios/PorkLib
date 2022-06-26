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
import net.daporkchop.lib.primitive.generator.param.Parameter;
import net.daporkchop.lib.primitive.generator.param.ParameterContext;
import net.daporkchop.lib.primitive.generator.param.ParameterValue;
import net.daporkchop.lib.primitive.generator.param.primitive.Primitive;
import net.daporkchop.lib.primitive.generator.replacer.ComplexGenericReplacer;
import net.daporkchop.lib.primitive.generator.replacer.FileHeaderReplacer;
import net.daporkchop.lib.primitive.generator.replacer.GenericHeaderReplacer;
import net.daporkchop.lib.primitive.generator.replacer.MethodReplacer;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
import static net.daporkchop.lib.common.util.PorkUtil.*;

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

    private static final Cached<Deque<StringBuffer>> STRINGBUFFER_CACHE = Cached.threadLocal(ArrayDeque::new, ReferenceStrength.WEAK);

    private static final Pattern TOKEN_PATTERN = Pattern.compile("_([a-zA-Z0-9,]*?)(?:([pP])(\\d+))?_");
    private static final Cached<Deque<Matcher>> TOKEN_MATCHER_CACHE = Cached.threadLocal(ArrayDeque::new, ReferenceStrength.WEAK);

    //recursive regular expressions! woot woot
    private static final com.florianingerl.util.regex.Pattern FILTER_PATTERN = com.florianingerl.util.regex.Pattern.compile(
            "(<(?:(?<generic>(?<generic_number>\\d+(?:,\\d+)*)?(?<generic_invert>!)?)|(?<type>(?<type_filter>(?:\\d+(?:[a-zA-Z]+|_(?:[a-zA-Z0-9,]*?)(?:([pP])(\\d+))?_))+)(?<type_invert>!)?))%(?<content>.*?(?:(?1).*?)*)%>)",
            com.florianingerl.util.regex.Pattern.DOTALL);
    private static final Cached<Deque<com.florianingerl.util.regex.Matcher>> FILTER_MATCHER_CACHE = Cached.threadLocal(ArrayDeque::new, ReferenceStrength.WEAK);

    private static final Pattern TYPE_FILTER_EXTRACT_PATTERN = Pattern.compile("(\\d+)([a-zA-Z]+)");
    private static final Cached<Matcher> TYPE_FILTER_EXTRACT_CACHE = Cached.regex(TYPE_FILTER_EXTRACT_PATTERN, true);

    private static StringBuffer allocateStringBuffer() {
        Deque<StringBuffer> stack = STRINGBUFFER_CACHE.get();
        return stack.isEmpty() ? new StringBuffer() : stack.pop();
    }

    private static void releaseStringBuffer(@NonNull StringBuffer buffer) {
        STRINGBUFFER_CACHE.get().push(buffer);
    }

    private static Matcher allocateTokenMatcher() {
        Deque<Matcher> stack = TOKEN_MATCHER_CACHE.get();
        return stack.isEmpty() ? TOKEN_PATTERN.matcher("") : stack.pop();
    }

    private static void releaseTokenMatcher(@NonNull Matcher matcher) {
        TOKEN_MATCHER_CACHE.get().push(matcher);
    }

    private static com.florianingerl.util.regex.Matcher allocateFilterMatcher() {
        Deque<com.florianingerl.util.regex.Matcher> stack = FILTER_MATCHER_CACHE.get();
        return stack.isEmpty() ? FILTER_PATTERN.matcher("") : stack.pop();
    }

    private static void releaseFilterMatcher(@NonNull com.florianingerl.util.regex.Matcher matcher) {
        FILTER_MATCHER_CACHE.get().push(matcher);
    }

    public static String replace(@NonNull Matcher matcher, @NonNull CharSequence original, @NonNull Function<? super Matcher, String> replacer) {
        StringBuffer buffer = new StringBuffer();

        matcher.reset(original);
        while (matcher.find()) {
            matcher.appendReplacement(buffer, replacer.apply(matcher));
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }

    public static String getPackageName(@NonNull Path p) {
        StringJoiner joiner = new StringJoiner(".");
        boolean started = false;
        for (Path path : p) {
            String fileName = path.toString();
            if (started) {
                if (fileName.endsWith(".template") || fileName.endsWith(".json") || fileName.endsWith(".methods")) {
                    break;
                } else {
                    joiner.add(fileName);
                }
            } else if ("java".equals(fileName)) {
                started = true;
            }
        }
        return joiner.toString();
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
    public final LongAdder timeFilter = new LongAdder();
    public final LongAdder timeReplaceToken = new LongAdder();
    public final LongAdder timeContentOverride = new LongAdder();
    public final LongAdder timeWrite = new LongAdder();

    public Generator(@NonNull Path inRoot, @NonNull Path outRoot) {
        this.inRoot = PFiles.assertDirectoryExists(inRoot);
        this.outRoot = PFiles.ensureDirectoryExists(outRoot);

        this.tokenReplacers = new TokenReplacer[]{
                new GenericHeaderReplacer(),
                new FileHeaderReplacer(),
                new ComplexGenericReplacer(),
                new MethodReplacer()
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
                new Tuple<>(this.timeFilter.sum(), "  Filter"),
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
                    if (!file.getFileName().toString().endsWith(".methods")) { //schedule task to asynchronously recurse into the subdirectory
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

    private void generateFile(@NonNull GeneratorConfig originalConfig, @NonNull FileTime latestExpectedTime, @NonNull Path templateFile, @NonNull BasicFileAttributes templateFileAttrs, @NonNull Path outDirectory) {
        PFiles.ensureDirectoryExists(outDirectory);

        Template template = this.loadTemplate(originalConfig, latestExpectedTime, templateFile, templateFileAttrs);

        Stream<List<? extends ParameterContext<?>>> parametersStream = Stream.of(Collections.emptyList());
        for (Parameter<?> parameter : template.getConfig().getParmeters().getParameters()) {
            parametersStream = parametersStream.flatMap(currentParams -> parameter.values().stream()
                    .map(value -> parameter.type().makeContext(uncheckedCast(parameter), uncheckedCast(value)))
                    .map(ctx -> {
                        List<ParameterContext<?>> params = new ArrayList<>(currentParams.size() + 1);
                        params.addAll(currentParams);
                        params.add(ctx);
                        return params;
                    }));
        }

        //spawn a sub-task for each combination of parameters, then execute them all and wait for them all to complete
        ForkJoinTask.invokeAll(parametersStream
                .map(params -> ForkJoinTask.adapt(() -> this.generate0(outDirectory, new Context(this, Optional.empty(), template, params))))
                .collect(Collectors.toList()));
    }

    @SneakyThrows(IOException.class)
    private Template loadTemplate(@NonNull GeneratorConfig inheritedConfig, @NonNull FileTime latestExpectedTime, @NonNull Path templateFile, @NonNull BasicFileAttributes templateFileAttrs) {
        String name = templateFile.getFileName().toString().substring(0, templateFile.getFileName().toString().length() - ".template".length());
        String content = new String(Files.readAllBytes(templateFile), StandardCharsets.UTF_8);

        Path configFile = PFiles.assertFileExists(templateFile.resolveSibling(name + ".json"));
        GeneratorConfig config = inheritedConfig.mergeConfiguration(new JsonParser().parse(new String(Files.readAllBytes(configFile), StandardCharsets.UTF_8)).getAsJsonObject());

        Map<String, Template> methods;
        Path methodsDir = templateFile.resolveSibling(name + ".methods");
        if (PFiles.checkDirectoryExists(methodsDir)) {
            methods = new HashMap<>();

            //list the files under the input directory and schedule sub-futures for them
            Files.walkFileTree(methodsDir, Collections.emptySet(), 1, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isRegularFile()) {
                        if (file.getFileName().toString().endsWith(".template")) { //schedule task to asynchronously generate the template
                            Template methodTemplate = Generator.this.loadTemplate(config, latestExpectedTime, file, attrs);
                            methods.put(methodTemplate.getTemplateFileName(), methodTemplate);
                        }
                    } else if (attrs.isDirectory() && file.getFileName().toString().endsWith(".methods")) {
                        //silently ignore
                    } else {
                        throw new IllegalArgumentException("don't know how to handle: " + file);
                    }

                    return super.visitFile(file, attrs);
                }
            });
        } else {
            methods = Collections.emptyMap();
        }

        FileTime expectedResultTime = Stream.concat(
                Stream.of(latestExpectedTime, templateFileAttrs.lastModifiedTime(), Files.getLastModifiedTime(configFile)),
                methods.values().stream().map(Template::getExpectedResultTime))
                .max(Comparator.naturalOrder()).get();

        return new Template(config, expectedResultTime, name, content, templateFile, methods);
    }

    @SneakyThrows(IOException.class)
    private void generate0(@NonNull Path outDirectory, @NonNull Context context) {
        String name;
        { //name
            long time = System.nanoTime(); //begin profiling timeName
            try {
                name = context.getConfig().getNameOverride().isPresent()
                        ? context.getConfig().getNameOverride().get()
                        : context.getTemplate().getTemplateFileName();

                name = this.processString(name, context);

                if (name.isEmpty()) {
                    return;
                }
            } finally {
                this.timeName.add(System.nanoTime() - time);
            }
        }

        { //name override
            long time = System.nanoTime();
            StringBuffer buffer = allocateStringBuffer();
            try {
                if ((name = context.getConfig().getOverrideReplacer().processName(name, -1, buffer)) == null) {
                    //the template file's name is supposed to be replaced!
                    return;
                }
                name += ".java";
            } finally {
                releaseStringBuffer(buffer);
                this.timeNameOverride.add(System.nanoTime() - time);
            }
        }

        Path file = outDirectory.resolve(name);
        checkState(this.generated.add(file), "File %s was already generated?!?", name);

        if (PFiles.checkFileExists(file) && Files.getLastModifiedTime(file).toMillis() == context.getTemplate().getExpectedResultTime().toMillis()) {
            return;
        }

        String contentOut = this.processString(context.getTemplate().getTemplateFileContent(), context);

        { //overrides
            long time = System.nanoTime();
            StringBuffer buffer = allocateStringBuffer();
            try {
                if ((contentOut = context.getConfig().getOverrideReplacer().processCode(contentOut, -1, buffer)) == null) {
                    throw new IllegalStateException();
                }
            } finally {
                releaseStringBuffer(buffer);
                this.timeContentOverride.add(System.nanoTime() - time);
            }
        }

        { //write
            long time = System.nanoTime();

            byte[] b = contentOut.getBytes(StandardCharsets.UTF_8);
            if (!PFiles.checkFileExists(file) || !Arrays.equals(Files.readAllBytes(file), b)) { //only (over)write file contents if the file doesn't exist or its contents are different
                Files.write(file, b);
            }
            Files.setLastModifiedTime(file, context.getTemplate().getExpectedResultTime());

            this.generatedSize.add(b.length);
            this.generatedFiles.increment();

            this.timeWrite.add(System.nanoTime() - time);
        }
    }

    public String processString(@NonNull String text, @NonNull Context context) {
        text = this.processFilters(text, context, true);
        text = this.processTokens(text, context);
        return text;
    }

    private String processFilters(@NonNull String text, @NonNull Context context, boolean first) {
        long time = System.nanoTime();

        com.florianingerl.util.regex.Matcher matcher = allocateFilterMatcher().reset(text);
        if (matcher.find()) {
            StringBuffer buffer = allocateStringBuffer();
            buffer.setLength(0);

            do {
                if (matcher.group("generic") != null) { //filter by generic types
                    String numberTxt = matcher.group("generic_number");
                    boolean generic = numberTxt == null
                            ? context.isAnyParameterGeneric() :
                            numberTxt.indexOf(',') >= 0
                                    ? Stream.of(numberTxt.split(",")).mapToInt(Integer::parseUnsignedInt).anyMatch(i -> ((Primitive) context.getParams().get(i).value()).generic)
                                    : ((Primitive) context.getParams().get(Integer.parseUnsignedInt(numberTxt)).value()).generic;
                    boolean inverted = matcher.group("generic_invert") != null;

                    if (generic ^ inverted) { //keep the enclosed contents
                        matcher.appendReplacement(buffer, this.processFilters(matcher.group("content"), context, false));
                    } else { //discard the enclosed contents
                        matcher.appendReplacement(buffer, "");
                    }
                } else if (matcher.group("type") != null) { //filter by type values
                    boolean inverted = matcher.group("type_invert") != null;
                    boolean valid = false;

                    Matcher extractMatcher = TYPE_FILTER_EXTRACT_CACHE.get().reset(this.processTokens(matcher.group("type_filter"), context));
                    while (extractMatcher.find()) {
                        int index = Integer.parseUnsignedInt(extractMatcher.group(1));
                        ParameterContext<?> parameterContext = context.getParams().get(index);
                        ParameterValue<?> value = parameterContext.parameter().type().getValuesByName().get(extractMatcher.group(2));
                        valid |= parameterContext.value().equals(value);
                    }

                    if (valid ^ inverted) { //keep the enclosed contents
                        matcher.appendReplacement(buffer, this.processFilters(matcher.group("content"), context, false));
                    } else { //discard the enclosed contents
                        matcher.appendReplacement(buffer, "");
                    }
                } else {
                    throw new IllegalStateException("invalid match: " + matcher.group());
                }
            } while (matcher.find());

            matcher.appendTail(buffer);
            text = buffer.toString();
            releaseStringBuffer(buffer);
        }
        releaseFilterMatcher(matcher);

        if (first) {
            this.timeFilter.add(System.nanoTime() - time);
        }
        return text;
    }

    private String processTokens(@NonNull String text, @NonNull Context context) {
        long time = System.nanoTime();

        Matcher matcher = allocateTokenMatcher().reset(text);
        if (matcher.find()) {
            StringBuffer buffer = allocateStringBuffer();
            buffer.setLength(0);
            MAIN:
            do {
                String original = matcher.group();
                String numberTxt = matcher.group(3);
                if (numberTxt != null) {
                    String token = matcher.group(1);
                    boolean lowerCase = matcher.group(2).charAt(0) == 'p';
                    String value = context.getParams().get(Integer.parseUnsignedInt(numberTxt)).replace(context, token, lowerCase);
                    if (value != null) {
                        matcher.appendReplacement(buffer, value);
                        continue;
                    }
                } else {
                    for (TokenReplacer replacer : this.tokenReplacers) {
                        String value = replacer.replace(context, original);
                        if (value != null) {
                            matcher.appendReplacement(buffer, this.processTokens(value, context));
                            continue MAIN;
                        }
                    }
                }
                matcher.appendReplacement(buffer, original);
            } while (matcher.find());
            matcher.appendTail(buffer);
            text = buffer.toString();
            releaseStringBuffer(buffer);
        }
        releaseTokenMatcher(matcher);

        this.timeReplaceToken.add(System.nanoTime() - time);
        return text;
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
