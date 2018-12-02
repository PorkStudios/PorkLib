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
import net.daporkchop.lib.minecraft.protocol.generator.ClassWriter;
import net.daporkchop.lib.minecraft.protocol.generator.DataGenerator;
import net.daporkchop.lib.minecraft.protocol.generator.obf.Mappings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class JavaGenerator implements DataGenerator {
    @NonNull
    private final File input;

    @Override
    public void run(@NonNull File out) throws IOException {
        Collection<Version> versions = Arrays.stream(this.input.listFiles())
                .filter(File::isFile)
                .map(file -> {
                    String version = file.getName().substring(0, file.getName().lastIndexOf('.'));
                    System.out.printf("  Reading data for version java -> v%s\n", version);
                    JsonObject object;
                    try (Reader reader = new InputStreamReader(new FileInputStream(file))) {
                        object = JSON_PARSER.parse(reader).getAsJsonArray().get(0).getAsJsonObject();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return new Version(object, version, version.replace('.', '_'));
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
        for (Map.Entry<String, JsonElement> entryPacket : version.object.getAsJsonObject("packets").getAsJsonObject("packet").entrySet()) {
            JsonObject packetObj = entryPacket.getValue().getAsJsonObject();
            String packetName = mappings.getClass(packetObj.get("class").getAsString().replace(".class", ""));
            packetName = packetName.substring(packetName.lastIndexOf('.') + 1, packetName.length());
            String owner = null;
            if (packetName.contains("$"))   {
                owner = packetName.substring(0, packetName.indexOf('$'));
                packetName = packetName.substring(packetName.indexOf('$') + 1, packetName.length());
                if (packetName.contains("Packet"))  {
                    packetName = String.format("S%s", packetName.substring(packetName.indexOf("Packet"), packetName.length()));
                } else {
                    packetName = String.format("%cPacket%s", "SERVERBOUND".equals(packetObj.get("direction").getAsString()) ? 'S' : 'C', packetName);
                }
            }
            try (ClassWriter writer = new ClassWriter(this.ensureFileExists(out, String.format("%s.java", packetName)))) {
                writer.write("@AllArgsConstructor",
                        "@NoArgsConstructor",
                        String.format("public class %s%s implements MinecraftPacket", packetName, owner == null ? "" : String.format(" extends %s", owner))).pushBraces()
                        .write("@Override",
                                "public PacketDirection getDirection()").pushBraces()
                        .write(String.format("return PacketDirection.%s;", packetObj.get("direction").getAsString())).pop().newline()
                        .write("@Override",
                                "public int getId()").pushBraces()
                        .write(String.format("return %d;", packetObj.get("id").getAsInt()));
            }
        }
    }

    //TODO: implement packets, we'll need to get searge mappings in here somehow

    @RequiredArgsConstructor
    private static class Version {
        @NonNull
        private final JsonObject object;
        @NonNull
        private final String version;
        @NonNull
        private final String versionDir;
    }
}
