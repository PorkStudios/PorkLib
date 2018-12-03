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

package net.daporkchop.lib.minecraft.protocol.generator.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class JavaGenerator implements DataGenerator, Logging {
    private static final Map<String, String> BURGER_URLS = new HashMap<String, String>() {
        {
            this.put("1.10", "https://raw.githubusercontent.com/Pokechu22/Burger/gh-pages/1.10.json");
            this.put("1.11.2", "https://raw.githubusercontent.com/Pokechu22/Burger/gh-pages/1.11.2.json");
            this.put("1.12.2", "https://raw.githubusercontent.com/Pokechu22/Burger/gh-pages/1.12.2.json");
        }
    };
    private static final Map<String, String> CLIENT_JAR_URLS = new HashMap<String, String>() {
        {
            this.put("1.10", "https://launcher.mojang.com/v1/objects/ba038efbc6d9e4a046927a7658413d0276895739/client.jar");
            this.put("1.11.2", "https://launcher.mojang.com/v1/objects/db5aa600f0b0bf508aaf579509b345c4e34087be/client.jar");
            this.put("1.12.2", "https://launcher.mojang.com/v1/objects/0f275bc1547d01fa5f56ba34bdc87d981ee12daf/client.jar");
        }
    };
    private static final Map<String, String> PACKET_FIELD_TYPES = new HashMap<String, String>() {
        {
            this.put("int", "int");
            this.put("varint", "int");
            this.put("boolean", "boolean");
            this.put("short", "short");
            this.put("long", "long");
            this.put("varlong", "long");
            this.put("byte", "byte");
            this.put("byte[]", "byte[]");
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
            this.put("L", (s, m) -> m.getClass(s.substring(1, s.length() - 1)));
            this.put("S", (s, m) -> "short");
            this.put("V", (s, m) -> "void");
            this.put("[", (s, m) -> String.format("%s[]", this.get(String.valueOf(s.charAt(1))).apply(s.substring(1, s.length()), m)));
        }
    };
    @NonNull
    private final File input;

    @Override
    public void run(@NonNull File out) throws IOException {
        Collection<Version> versions = BURGER_URLS.keySet().stream()
                .map(s -> new File(this.input, s))
                .map(file -> {
                    try {
                        String version = file.getName().substring(0, file.getName().length());
                        System.out.printf("  Reading data for java -> v%s\n", version);
                        JsonObject object;
                        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(Cache.INSTANCE.getBytes(new File(file, this.format("${0}.json", version)), BURGER_URLS.get(version))))) {
                            object = JSON_PARSER.parse(reader).getAsJsonArray().get(0).getAsJsonObject();
                        }
                        ZipFile clientJar = new ZipFile(Cache.INSTANCE.getFile(new File(file, this.format("${0}.jar", version)), CLIENT_JAR_URLS.get(version)));
                        return new Version(object, version, version.replace('.', '_'), clientJar);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        try (ClassWriter writer = new ClassWriter(this.ensureFileExists(out, "JavaPlatform.java"))) {
            writer.write("@NoArgsConstructor(access = AccessLevel.PRIVATE)",
                    "public class JavaPlatform implements Platform").pushBraces()
                    .write("public static final JavaPlatform INSTANCE = new JavaPlatform();").newline()
                    .write("private static final Collection<Version> VERSIONS = Stream.of(null").push();
            for (Version version : versions) {
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

        for (Version version : versions) {
            this.generate(version, new File(out, String.format("v%s", version.versionDir)));
        }

        //TODO: my amazing formatter from dev/network-revamp-v6 would be nice here
    }

    private void generate(@NonNull Version version, @NonNull File out) throws IOException {
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

    private void generateSounds(@NonNull Version version, @NonNull File out) throws IOException {
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

    private void generateBiomes(@NonNull Version version, @NonNull File out) throws IOException {
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

    private void generateItems(@NonNull Version version, @NonNull File out) throws IOException {
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

    private void generateLocales(@NonNull Version version, @NonNull File out) throws IOException {
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

    private void generatePackets(@NonNull Version version, @NonNull File out) throws IOException {
        Mappings mappings = Mappings.getMappings(version.version);
        mappings.getClasses().entrySet().stream()
                .filter(entry -> entry.getValue().startsWith("net.minecraft.network"))
                .filter(entry -> entry.getValue().contains("SPacket") || entry.getValue().contains("CPacket"))
                .forEach(entry -> {
                    logger.trace("Packet class: ${0} -> ${1}", entry.getKey(), entry.getValue());
                    try (InputStream in = version.clientJar.getInputStream(version.clientJar.getEntry(this.format("${0}.class", entry.getKey())))) {
                        ClassReader reader = new ClassReader(in);
                        if ((reader.getAccess() & Opcodes.ACC_ENUM) != 0) {
                            logger.trace(" Enum: ${0}", entry.getValue());
                        }
                        reader.accept(new ClassVisitor(Opcodes.ASM4) {
                            @Override
                            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                                BiFunction<String, Mappings, String> func = NAME_TRANSLATORS.get(String.valueOf(desc.charAt(0)));
                                StringJoiner joiner = new StringJoiner(" ");
                                if ((access & Opcodes.ACC_PUBLIC) != 0) {
                                    joiner.add("public");
                                }
                                if ((access & Opcodes.ACC_PRIVATE) != 0) {
                                    joiner.add("private");
                                }
                                if ((access & Opcodes.ACC_PROTECTED) != 0) {
                                    joiner.add("protected");
                                }
                                if ((access & Opcodes.ACC_STATIC) != 0) {
                                    joiner.add("static");
                                }
                                if ((access & Opcodes.ACC_FINAL) != 0) {
                                    joiner.add("final");
                                }
                                if ((access & Opcodes.ACC_TRANSIENT) != 0) {
                                    joiner.add("transient");
                                }
                                if ((access & Opcodes.ACC_VOLATILE) != 0) {
                                    joiner.add("volatile");
                                }
                                if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
                                    joiner.add("synthetic");
                                }

                                logger.debug("    ${0} ${2} ${1}", joiner.toString(), mappings.getField(entry.getValue(), name), func.apply(desc, mappings).replace('/', '.'));
                                return super.visitField(access, name, desc, signature, value);
                            }
                        }, 0);
                    } catch (IOException e) {
                        throw this.exception(e);
                    }
                });
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
    }

    @Getter
    private static class FieldFindingClassVisitor extends ClassVisitor {
        private final String classNameMCP;
        private final String classNameObf;
        private final Collection<Field> fields = new ArrayDeque<>();

        public FieldFindingClassVisitor(@NonNull String classNameMCP, @NonNull String classNameObf) {
            super(Opcodes.ASM6);
            this.classNameMCP = classNameMCP;
            this.classNameObf = classNameObf;
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            BiFunction<String, Mappings, String> func = NAME_TRANSLATORS.get(String.valueOf(desc.charAt(0)));

            return super.visitField(access, name, desc, signature, value);
        }
    }

    @RequiredArgsConstructor
    @Getter
    private static class Field {
        @NonNull
        private final String name;
        private final Collection<AccessModifier> modifiers = new ArrayDeque<>();
    }

    @RequiredArgsConstructor
    private static class Version {
        @NonNull
        private final JsonObject object;
        @NonNull
        private final String version;
        @NonNull
        private final String versionDir;
        @NonNull
        private final ZipFile clientJar;
    }
}
