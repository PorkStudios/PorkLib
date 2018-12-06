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

package net.daporkchop.lib.minecraft.protocol.generator.data.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.minecraft.protocol.generator.Cache;
import net.daporkchop.lib.minecraft.protocol.generator.ClassWriter;
import net.daporkchop.lib.minecraft.protocol.generator.DataGenerator;
import net.daporkchop.lib.minecraft.protocol.generator.obf.Mappings;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.ZipFile;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class JavaGenerator implements DataGenerator, Logging {
    private static final Map<String, String> PACKET_IO_NAMES = new HashMap<String, String>() {
        {
            this.put("int", "Int");
            this.put("varint", "MojangVarInt");
            this.put("boolean", "Boolean");
            this.put("short", "Short");
            this.put("long", "Long");
            this.put("varlong", "MojangVarLong");
            this.put("byte", "");
            this.put("byte[]", "");
            this.put("string", "MojangString");
            this.put("float", "Float");
            this.put("double", "Double");
        }
    };
    private static final Map<String, BiFunction<String, Mappings, String>> NAME_TRANSLATORS = new HashMap<String, BiFunction<String, Mappings, String>>() {
        {
            this.put("Z", (s, m) -> "boolean");
            this.put("B", (s, m) -> "byte");
            this.put("C", (s, m) -> "char");
            this.put("D", (s, m) -> "double");
            this.put("F", (s, m) -> "float");
            this.put("I", (s, m) -> "int");
            this.put("J", (s, m) -> "long");
            this.put("L", (s, m) -> {
                String s1 = m.getClass(s.substring(1, s.length() - 1)).replace('/', '.');
                if (s1.contains("$")) {
                    return s1.substring(s1.lastIndexOf('$') + 1, s1.length());
                } else {
                    return s1;
                }
            });
            this.put("S", (s, m) -> "short");
            this.put("V", (s, m) -> "void");
            this.put("[", (s, m) -> String.format("%s[]", this.get(String.valueOf(s.charAt(1))).apply(s.substring(1, s.length()), m)));
        }
    };
    @NonNull
    private final File input;

    @Override
    public void run(@NonNull File out) throws IOException {
        Collection<JavaVersion> versions = JavaVersion.VERSIONS.stream()
                .map(version -> JavaVersion.builder()
                        .version(version)
                        .versionDir(version.replace('.', '_')))
                .map(builder -> {
                    System.out.printf("  Reading data for java -> v%s\n", builder.version);
                    try (Reader reader = new InputStreamReader(new ByteArrayInputStream(Cache.INSTANCE.getBytes(Cache.InfoType.JAVA_BURGER, builder.version)))) {
                        return builder.object(JSON_PARSER.parse(reader).getAsJsonArray().get(0).getAsJsonObject());
                    } catch (IOException e) {
                        throw this.exception(e);
                    }
                })
                .map(builder -> {
                    try {
                        return builder.clientJar(new ZipFile(Cache.INSTANCE.getFile(Cache.InfoType.JAVA_CLIENT, builder.version)));
                    } catch (IOException e) {
                        throw this.exception(e);
                    }
                })
                .map(builder -> builder.mappings(Mappings.getMappings(builder.version)))
                .map(JavaVersion.JavaVersionBuilder::build)
                .collect(Collectors.toList());

        try (ClassWriter writer = new ClassWriter(this.ensureFileExists(out, "JavaPlatform.java"))) {
            writer.write("@NoArgsConstructor(access = AccessLevel.PRIVATE)",
                    "public class JavaPlatform implements Platform").pushBraces()
                    .write("public static final JavaPlatform INSTANCE = new JavaPlatform();").newline()
                    .write("private static final Collection<Version> VERSIONS = Stream.of(null").push();
            for (JavaVersion version : versions) {
                writer.write(String.format(", net.daporkchop.lib.minecraft.protocol.mc.java.v%s.Java%s.INSTANCE", version.versionDir, version.versionDir));
            }
            writer.pop().write(").filter(Objects::nonNull).collect(Collectors.toList());")
                    .newline().write("@Override",
                    "public String getName()").pushBraces()
                    .write("return \"Minecraft: Java Edition\"; //actually just minecraft but i'll go with this new fake gay name because mojang decided it's now called that").pop()
                    .newline().write("@Override",
                    "public Collection<Version> getVersions()").pushBraces()
                    .write("return VERSIONS;");
        }

        for (JavaVersion version : versions) {
            this.generate(version, new File(out, String.format("v%s", version.versionDir)));
        }

        //TODO: my amazing formatter from dev/network-revamp-v6 would be nice here
    }

    private void generate(@NonNull JavaVersion version, @NonNull File out) throws IOException {
        System.out.printf("  Generating classes for java -> %s\n", version.version);
        try (ClassWriter writer = new ClassWriter(this.ensureFileExists(out, String.format("Java%s.java", version.versionDir)))) {
            writer.write("@NoArgsConstructor(access = AccessLevel.PRIVATE)",
                    String.format("public class Java%s implements Version", version.versionDir)).pushBraces()
                    .write(String.format("public static final Java%s INSTANCE = new Java%s();", version.versionDir, version.versionDir)).newline()
                    .write("@Override",
                            "public Platform getPlatform()").pushBraces()
                    .write("return net.daporkchop.lib.minecraft.protocol.mc.java.JavaPlatform.INSTANCE;").pop().newline()
                    .write("@Override",
                            "public String getName()").pushBraces()
                    .write(String.format("return \"Java v%s\";", version.version)).pop().newline()
                    .write("@Override",
                            "public int getProtocolVersion()").pushBraces()
                    .write(String.format("return %d;", version.object.getAsJsonObject("version").get("protocol").getAsInt())).pop().newline()
                    .write("@Override",
                            "public Sound[] getSounds()").pushBraces()
                    .write(String.format("return Sounds%s.values();", version.versionDir)).pop().newline()
                    .write("@Override",
                            "public Item[] getItems()").pushBraces()
                    .write(String.format("return Items%s.values();", version.versionDir)).pop().newline()
                    .write("@Override",
                            "public Biome[] getBiomes()").pushBraces()
                    .write(String.format("return Biomes%s.values();", version.versionDir));
        }

        this.generateSounds(version, out);
        this.generateBiomes(version, out);
        this.generateItems(version, out);
        this.generateLocales(version, new File(out, "locale"));
        this.generatePackets(version, new File(out, "packet"));
    }

    private void generateSounds(@NonNull JavaVersion version, @NonNull File out) throws IOException {
        try (ClassWriter writer = new ClassWriter(this.ensureFileExists(out, String.format("Sounds%s.java", version.versionDir)))) {
            writer.write("@RequiredArgsConstructor",
                    "@Getter",
                    String.format("public enum Sounds%s implements Sound", version.versionDir)).pushBraces();
            for (Map.Entry<String, JsonElement> entry : version.object.getAsJsonObject("sounds").entrySet()) {
                //kinda want to use a stream here but then i'd have to make yet another try/catch
                writer.write(String.format(
                        "%s(\"%s\", %d),",
                        entry.getKey().replace('.', '_').toUpperCase(),
                        entry.getKey(),
                        entry.getValue().getAsJsonObject().get("id").getAsInt()
                ));
            }
            writer.write(";").newline()
                    .write("@NonNull",
                            "private final String name;",
                            "private final int id;");
        }
    }

    private void generateBiomes(@NonNull JavaVersion version, @NonNull File out) throws IOException {
        try (ClassWriter writer = new ClassWriter(this.ensureFileExists(out, String.format("Biomes%s.java", version.versionDir)),
                "net.daporkchop.lib.minecraft.registry.*")) {
            writer.write("@RequiredArgsConstructor",
                    "@Getter",
                    String.format("public enum Biomes%s implements Biome", version.versionDir)).pushBraces();
            for (Map.Entry<String, JsonElement> entry : version.object.getAsJsonObject("biomes").getAsJsonObject("biome").entrySet()) {
                JsonObject obj = entry.getValue().getAsJsonObject();
                JsonArray heights = obj.getAsJsonArray("height");
                writer.write(String.format(
                        "%s(new ResourceLocation(\"minecraft\", \"%s\"), %d, %ff, %ff, %ff, %ff),",
                        entry.getKey().toUpperCase(),
                        entry.getKey(),
                        obj.get("id").getAsInt(),
                        heights.get(0).getAsDouble(),
                        heights.get(1).getAsDouble(),
                        obj.get("rainfall").getAsDouble(),
                        obj.get("temperature").getAsDouble()
                ));
            }
            writer.write(";").newline()
                    .write("@NonNull",
                            "private final ResourceLocation registryName;",
                            "private final int id;",
                            "private final float minHeight;",
                            "private final float maxHeight;",
                            "private final float rainfall;",
                            "private final float temperature;");
        }
    }

    private void generateItems(@NonNull JavaVersion version, @NonNull File out) throws IOException {
        try (ClassWriter writer = new ClassWriter(this.ensureFileExists(out, String.format("Items%s.java", version.versionDir)),
                "net.daporkchop.lib.minecraft.registry.*")) {
            writer.write("@RequiredArgsConstructor",
                    "@Getter",
                    String.format("public enum Items%s implements Item", version.versionDir)).pushBraces();
            for (Map.Entry<String, JsonElement> entry : version.object.getAsJsonObject("items").getAsJsonObject("item").entrySet()) {
                JsonObject obj = entry.getValue().getAsJsonObject();
                String maxStackSize = obj.has("max_stack_size") ? String.valueOf(obj.get("max_stack_size").getAsInt()) : String.format("net.daporkchop.lib.minecraft.protocol.mc.java.v1_12_2.Items1_12_2.%s.getMaxStackSize()", entry.getKey().toUpperCase());
                writer.write(String.format(
                        "%s(new ResourceLocation(\"minecraft\", \"%s\"), %d, %s),",
                        entry.getKey().toUpperCase(),
                        entry.getKey(),
                        obj.get("numeric_id").getAsInt(),
                        maxStackSize
                ));
            }
            writer.write(";").newline()
                    .write("@NonNull",
                            "private final ResourceLocation registryName;",
                            "private final int id;",
                            "private final int maxStackSize;");
        }
    }

    private void generateLocales(@NonNull JavaVersion version, @NonNull File out) throws IOException {
        try (ClassWriter writer = new ClassWriter(this.ensureFileExists(out, "EN_US.java"))) {
            writer.write("@NoArgsConstructor(access = AccessLevel.PRIVATE)",
                    "@Getter",
                    "public class EN_US implements net.daporkchop.lib.minecraft.protocol.api.data.Locale").pushBraces()
                    .write("public static final EN_US INSTANCE = new EN_US();").newline().pushBraces();
            for (Map.Entry<String, JsonElement> entry1 : version.object.get("language").getAsJsonObject().entrySet()) {
                for (Map.Entry<String, JsonElement> entry2 : entry1.getValue().getAsJsonObject().entrySet()) {
                    writer.write(String.format(
                            "this.underlyingMap.put(\"%s\", \"%s\");",
                            entry2.getKey(),
                            entry2.getValue().getAsString().replace("\"", "\\\"")
                    ));
                }
            }
            writer.pop().newline()
                    .write("private final Map<String, String> underlyingMap = new HashMap();");
        }
    }

    private void generatePackets(@NonNull JavaVersion version, @NonNull File out) throws IOException {
        Map<String, JsonObject> toBurgerMappings = new HashMap<>();
        version.object.getAsJsonObject("packets").getAsJsonObject("packet").entrySet().stream()
                .map(Map.Entry::getValue)
                .map(JsonElement::getAsJsonObject)
                .forEach(jsonObject -> {
                    String className = jsonObject.get("class").getAsString();
                    toBurgerMappings.put(className.substring(0, className.indexOf('.')), jsonObject);
                });
        Collection<JavaClass> packetClasses = new ArrayDeque<>();
        version.mappings.getClasses().entrySet().stream()
                .filter(entry -> entry.getValue().startsWith("net.minecraft.network"))
                .filter(entry -> entry.getValue().contains("SPacket") || entry.getValue().contains("CPacket"))
                .forEach(entry -> {
                    try (InputStream in = version.clientJar.getInputStream(version.clientJar.getEntry(this.format("${0}.class", entry.getKey())))) {
                        ClassReader reader = new ClassReader(in);
                        JavaClass.JavaClassBuilder classBuilder = JavaClass.builder()
                                .nameObf(entry.getKey())
                                .nameMCP(entry.getValue())
                                .isEnum((reader.getAccess() & Opcodes.ACC_ENUM) != 0);
                        reader.accept(new FieldFindingClassVisitor(classBuilder, version.mappings), 0);
                        packetClasses.add(classBuilder.build());
                    } catch (IOException e) {
                        throw this.exception(e);
                    } catch (NullPointerException e) {
                        throw this.exception("Invalid class name: ${0}", entry.getKey());
                    }
                });

        packetClasses.stream()
                .filter(c -> c.nameMCP.contains("$"))
                .forEach(clazz -> {
                    logger.info("Found subclass: ${0} -> ${1}", clazz.nameObf, clazz.nameMCP);
                    String[] split = clazz.nameMCP.split("\\$");
                    try {
                        Integer.parseInt(split[1]);
                        logger.trace("Invalid class name: ${0}", clazz.nameMCP);
                    } catch (NumberFormatException e) {
                        JavaClass parentClass = packetClasses.stream()
                                .filter(clazz1 -> clazz1.nameMCP.equals(split[0]))
                                .findAny().get();
                        parentClass.subClasses.add(clazz);
                        clazz.setParent(parentClass);
                    }
                });
        packetClasses.removeIf(clazz -> clazz.nameMCP.contains("$"));

        packetClasses.stream()
                .filter(JavaClass::doesntHaveSyntheticField)
                .forEach(clazz -> {
                    String className = clazz.nameMCP.substring(clazz.nameMCP.lastIndexOf('.') + 1, clazz.nameMCP.length());
                    try (ClassWriter writer = new ClassWriter(this.ensureFileExists(new File(out, String.format("%s.java", className))))) {
                        this.writePacket(clazz, version.mappings, toBurgerMappings, writer, false, null, null);
                    } catch (IOException e) {
                        throw this.exception(e);
                    }
                });
    }

    private void writePacket(@NonNull JavaClass clazz, @NonNull Mappings mappings, @NonNull Map<String, JsonObject> toBurgerMappings, @NonNull ClassWriter writer, boolean sub, String parentName, JavaClass parent) throws IOException {
        if (sub) {
            writer.newline();
        }
        if (!sub && clazz.isEnum) {
            throw this.exception("Base class ${0} is not a subclass!");
        }
        String className = clazz.nameMCP.substring(clazz.nameMCP.lastIndexOf(sub ? '$' : '.') + 1, clazz.nameMCP.length());
        if (clazz.isEnum) {
            writer.write(String.format("public enum %s", className)).pushBraces();
            for (JavaField field : clazz.fields) {
                writer.write(String.format("%s,", field.nameMCP));
            }
            writer.write(";").pop();
        } else {
            JsonObject burger = toBurgerMappings.get(clazz.nameObf);
            if (burger == null || !burger.has("id")) {
                logger.trace("Skipping packet: ${0} -> ${1}", clazz.nameObf, clazz.nameMCP);
                return;
            }
            if (clazz.fields.isEmpty() || sub) {
                writer.write("@NoArgsConstructor");
            } else {
                writer.write("@AllArgsConstructor", "@NoArgsConstructor");
            }
            writer.write(String.format("public %sclass %s %s %s", sub ? "static " : "", className, sub ? "extends" : "implements", sub ? parentName : "MinecraftPacket")).pushBraces();
            writer.write(String.format("public static final int ID = %d;", burger.get("id").getAsInt()));

            if (sub) {
                if (!parent.fields.isEmpty()) {
                    Collection<JavaField> mergedFields = new ArrayDeque<>(parent.fields);
                    mergedFields.addAll(clazz.fields);
                    StringJoiner joiner = new StringJoiner(", ");
                    mergedFields.stream().map(field -> String.format("%s %s", field.type, field.nameMCP)).forEachOrdered(joiner::add);
                    writer.newline().write(String.format("public %s(%s)", className, joiner.toString())).pushBraces();
                    joiner = new StringJoiner(", ");
                    parent.fields.stream().map(JavaField::getNameMCP).forEach(joiner::add);
                    writer.write(String.format("super(%s);", joiner.toString()));
                    for (JavaField field : clazz.fields) {
                        writer.write(this.format("this.${0} = ${0};", field.nameMCP));
                    }
                    writer.pop();
                }
            }

            if (!clazz.fields.isEmpty()) {
                writer.newline();
                for (JavaField field : clazz.fields) {
                    writer.write(String.format("public %s %s;", field.type, field.nameMCP));
                }
            }

            writer.newline()
                    .write("@Override",
                            "public void write(@NonNull DataOut out) throws IOException").pushBraces().pop();
            //this.writePacketInstructions(burger.getAsJsonArray("instructions"), writer, mappings, clazz);

            writer.newline()
                    .write("@Override",
                            "public void read(@NonNull DataIn in) throws IOException").pushBraces().pop();
            //this.readPacketInstructions(burger.getAsJsonArray("instructions"), writer, mappings, clazz);

            writer.newline()
                    .write("@Override",
                            "public int getId()").pushBraces()
                    .write("return ID;").pop();

            writer.newline().write("@Override",
                    "public PacketDirection getDirection()").pushBraces()
                    .write(String.format("return PacketDirection.%s;", burger.get("direction").getAsString())).pop();

            for (JavaClass subClass : clazz.subClasses) {
                this.writePacket(subClass, mappings, toBurgerMappings, writer, true, className, clazz);
            }
            writer.pop();
        }
    }

    private void writePacketInstructions(@NonNull JsonArray array, @NonNull ClassWriter writer, @NonNull Mappings mappings, @NonNull JavaClass clazz) throws IOException {
        writer.pushBraces();
        for (JsonObject object : StreamSupport.stream(array.spliterator(), false).map(JsonElement::getAsJsonObject).collect(Collectors.toList())) {
            String operation = object.get("operation").getAsString();
            switch (operation) {
                case "write": {
                    String field = object.get("field").getAsString();
                    String type = object.get("type").getAsString();
                    if (field.contains(".")) {
                        int i = field.indexOf('.');
                        field = String.format("%s%s", clazz.deobfuscate(field.substring(0, i), mappings), field.substring(i, field.length()));
                    } else if (field.startsWith("(")) {
                        int i = field.indexOf(')');
                        field = String.format("%s%s", clazz.deobfuscate(field.substring(1, i), mappings), field.substring(i + 1, field.length()));
                    } else {
                        field = clazz.deobfuscate(field, mappings);
                    }
                    if (field.contains(" ? 1 : 0")) {
                        field = field.replace(" ? 1 : 0", "");
                        type = "boolean";
                    }
                    String func = PACKET_IO_NAMES.get(type);
                    writer.write(String.format(
                            "%sout.write%s(%s);",
                            func == null ? "// " : "",
                            func == null ? String.format(" \"%s\" ", type) : func,
                            field
                    ));
                }
                break;
            }
        }
        writer.pop();
    }

    private void readPacketInstructions(@NonNull JsonArray array, @NonNull ClassWriter writer, @NonNull Mappings mappings, @NonNull JavaClass clazz) throws IOException {
        writer.pushBraces();
        for (JsonObject object : StreamSupport.stream(array.spliterator(), false).map(JsonElement::getAsJsonObject).collect(Collectors.toList())) {
            String operation = object.get("operation").getAsString();
            switch (operation) {
                case "write": {
                    String field = object.get("field").getAsString();
                    String type = object.get("type").getAsString();
                    if (field.contains(".")) {
                        int i = field.indexOf('.');
                        field = String.format("%s%s", clazz.deobfuscate(field.substring(0, i), mappings), field.substring(i, field.length()));
                    } else if (field.startsWith("(")) {
                        int i = field.indexOf(')');
                        field = String.format("%s%s", clazz.deobfuscate(field.substring(1, i), mappings), field.substring(i + 1, field.length()));
                    } else {
                        field = clazz.deobfuscate(field, mappings);
                    }
                    if (field.contains(" ? 1 : 0")) {
                        field = field.replace(" ? 1 : 0", "");
                        type = "boolean";
                    }
                    String func = PACKET_IO_NAMES.get(type);
                    writer.write(String.format(
                            "%s%s = in.read%s();",
                            func == null ? "// " : "",
                            field,
                            func == null ? String.format(" \"%s\" ", type) : func
                    ));
                }
                break;
            }
        }
        writer.pop();
    }

    @RequiredArgsConstructor
    @Getter
    private enum AccessModifier {
        PUBLIC(Opcodes.ACC_PUBLIC),
        PRIVATE(Opcodes.ACC_PRIVATE),
        PROTECTED(Opcodes.ACC_PROTECTED),
        STATIC(Opcodes.ACC_STATIC),
        FINAL(Opcodes.ACC_FINAL),
        TRANSIENT(Opcodes.ACC_TRANSIENT),
        VOLATILE(Opcodes.ACC_VOLATILE),
        SYNTHETIC(Opcodes.ACC_SYNTHETIC);

        private final int mask;

        private static void forEachModifier(int flags, @NonNull Consumer<AccessModifier> consumer) {
            for (AccessModifier modifier : values()) {
                if ((flags & modifier.mask) != 0) {
                    consumer.accept(modifier);
                }
            }
        }
    }

    @Getter
    private static class FieldFindingClassVisitor extends ClassVisitor {
        private final JavaClass.JavaClassBuilder classBuilder;
        private final Mappings mappings;

        public FieldFindingClassVisitor(@NonNull JavaClass.JavaClassBuilder classBuilder, @NonNull Mappings mappings) {
            super(Opcodes.ASM6);
            this.classBuilder = classBuilder;
            this.mappings = mappings;
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            BiFunction<String, Mappings, String> func = NAME_TRANSLATORS.get(String.valueOf(desc.charAt(0)));
            JavaField.JavaFieldBuilder fieldBuilder = JavaField.builder()
                    .nameObf(name)
                    .nameMCP(this.mappings.getField(this.classBuilder.nameMCP, name))
                    .type(func.apply(desc, this.mappings));
            if (fieldBuilder.nameMCP.startsWith("$")) {
                return super.visitField(access, name, desc, signature, value);
            }
            AccessModifier.forEachModifier(access, fieldBuilder::modifier);
            this.classBuilder.field(fieldBuilder.build());
            return super.visitField(access, name, desc, signature, value);
        }
    }

    @Builder
    @Getter
    private static class JavaField {
        @NonNull
        private final String nameMCP;
        @NonNull
        private final String nameObf;
        @NonNull
        private final String type;
        @Singular
        private final Collection<AccessModifier> modifiers;
    }

    @Builder
    @Getter
    private static class JavaClass {
        @NonNull
        private final String nameMCP;
        @NonNull
        private final String nameObf;
        @Singular
        private final Collection<JavaField> fields;
        private final boolean isEnum;
        @Builder.Default
        private final Collection<JavaClass> subClasses = new ArrayDeque<>();
        @Setter
        private JavaClass parent;

        public boolean hasSyntheticField() {
            for (JavaField field : this.fields) {
                if (field.modifiers.contains(AccessModifier.SYNTHETIC)) {
                    return true;
                }
            }
            return false;
        }

        public boolean doesntHaveSyntheticField() {
            return !this.hasSyntheticField();
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(this.nameObf);
            builder.append(" -> ");
            builder.append(this.nameMCP);
            builder.append('\n');
            for (JavaField field : this.fields) {
                builder.append("  ");
                for (AccessModifier modifier : field.modifiers) {
                    builder.append(modifier.name().toLowerCase());
                    builder.append(' ');
                }
                builder.append(field.type);
                builder.append(' ');
                builder.append(field.nameMCP);
                builder.append('\n');
            }
            return builder.toString();
        }

        public String deobfuscate(@NonNull String s, @NonNull Mappings mappings) {
            JavaClass clazz = this;
            String s1;
            do {
                s1 = mappings.getField(clazz.getNameMCP(), s);
                clazz = clazz.parent;
            } while (s1 == null && clazz != null);
            return s1 == null ? s : s1;
        }
    }

    /**
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    @Builder
    public static class JavaVersion {
        public static final Collection<String> VERSIONS = Arrays.asList(
                "1.10",
                "1.11.2",
                "1.12.2"
        );

        @NonNull
        public final JsonObject object;
        @NonNull
        public final String version;
        @NonNull
        public final String versionDir;
        @NonNull
        public final ZipFile clientJar;
        @NonNull
        private final Mappings mappings;
    }
}
