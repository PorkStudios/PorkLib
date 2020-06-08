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

package net.daporkchop.lib.minecraft.block.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.function.PFunctions;
import net.daporkchop.lib.minecraft.block.BlockRegistry;
import net.daporkchop.lib.minecraft.block.Property;
import net.daporkchop.lib.minecraft.block.property.BooleanPropertyImpl;
import net.daporkchop.lib.minecraft.block.property.EnumPropertyImpl;
import net.daporkchop.lib.minecraft.block.property.IntPropertyImpl;
import net.daporkchop.lib.minecraft.format.common.block.legacy.LegacyBlockRegistry;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.primitive.map.ObjIntMap;
import net.daporkchop.lib.primitive.map.open.ObjIntOpenHashMap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class JavaBlockRegistry1_12_2 {
    public static final BlockRegistry INSTANCE;

    static {
        JsonArray root;
        try (Reader reader = new InputStreamReader(JavaBlockRegistry1_12_2.class.getResourceAsStream("1.12.2.json"), StandardCharsets.UTF_8)) {
            root = new JsonParser().parse(reader).getAsJsonArray();
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        LegacyBlockRegistry.Builder builder = LegacyBlockRegistry.builder();
        for (JsonObject obj : StreamSupport.stream(root.spliterator(), false).map(JsonElement::getAsJsonObject).collect(Collectors.toList())) {
            LegacyBlockRegistry.BlockBuilder block = builder.startBlock(Identifier.fromString(obj.get("name").getAsString()));
            Map<String, Property<?>> propertyMap = StreamSupport.stream(obj.getAsJsonArray("properties").spliterator(), false)
                    .map(JsonElement::getAsJsonObject)
                    .collect(Collectors.toMap(o -> o.get("name").getAsString().intern(), JavaBlockRegistry1_12_2::parseProperty));
            block.properties(propertyMap.values().toArray(new Property[propertyMap.size()]));

            ObjIntMap<Map<Property<?>, ?>> stateToMeta = new ObjIntOpenHashMap<>();
            StreamSupport.stream(obj.getAsJsonArray("states").spliterator(), false)
                    .map(JsonElement::getAsJsonObject)
                    .forEach(state -> stateToMeta.put(parseProperties(propertyMap, state.getAsJsonObject("properties")), state.get("meta").getAsInt()));
            block.stateToMeta(stateToMeta);

            @SuppressWarnings("unchecked")
            Map<Property<?>, ?>[] metaToState = StreamSupport.stream(obj.getAsJsonArray("metas").spliterator(), false)
                    .filter(PFunctions.not(JsonElement::isJsonNull))
                    .map(JsonElement::getAsJsonObject)
                    .map(o -> parseProperties(propertyMap, o))
                    .toArray(Map[]::new);
            block.metaToState(metaToState);

            block.defaultState(parseProperties(propertyMap, obj.getAsJsonObject("defaultState")))
                    .legacyId(obj.get("legacyId").getAsInt());
        }
        INSTANCE = builder.build();
    }

    protected Property<?> parseProperty(@NonNull JsonObject obj) {
        String name = obj.get("name").getAsString().intern();
        JsonArray values = obj.get("values").getAsJsonArray();
        switch (obj.get("type").getAsString()) {
            case "int":
                return new IntPropertyImpl(name, StreamSupport.stream(values.spliterator(), false)
                        .mapToInt(JsonElement::getAsInt).min().orElse(0), StreamSupport.stream(values.spliterator(), false)
                        .mapToInt(JsonElement::getAsInt).max().orElse(0) + 1);
            case "boolean":
                return new BooleanPropertyImpl(name);
            case "enum":
                return new EnumPropertyImpl(name, StreamSupport.stream(values.spliterator(), false)
                        .map(JsonElement::getAsString).collect(Collectors.toList()));
        }
        throw new IllegalArgumentException(obj.get("type").getAsString());
    }

    protected Map<Property<?>, ?> parseProperties(@NonNull Map<String, Property<?>> propertyMap, @NonNull JsonObject object) {
        Map<Property<?>, ?> properties = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            Property<?> property = propertyMap.get(entry.getKey());
            properties.put(property, uncheckedCast(property.decodeValue(entry.getValue().getAsString())));
        }
        return properties;
    }
}
