/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.minecraft.text.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.common.pool.StaticPool;
import net.daporkchop.lib.logging.console.TextFormat;
import net.daporkchop.lib.logging.format.TextStyle;
import net.daporkchop.lib.logging.format.component.TextComponentString;
import net.daporkchop.lib.minecraft.text.MCTextFormat;
import net.daporkchop.lib.minecraft.text.MCTextType;
import net.daporkchop.lib.minecraft.text.component.MCTextRoot;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 * @see net.daporkchop.lib.minecraft.text.MCTextType#JSON
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class JsonTextParser {
    protected static final JsonParser PARSER = StaticPool.getInstance(JsonParser.class);
    protected static final Map<String, Color> COLOR_LOOKUP = new HashMap<>();

    static {
        for (MCTextFormat format : MCTextFormat.COLOR_CODES) {
            Color color = new Color(format.getColor());
            COLOR_LOOKUP.put(format.name().toUpperCase().intern(), color);
            COLOR_LOOKUP.put(format.name().toLowerCase().intern(), color);
            COLOR_LOOKUP.put(String.valueOf(format.getCode()).intern(), color);
        }
    }

    public static MCTextRoot parse(@NonNull String raw) {
        try {
            return parse(PARSER.parse(raw), raw);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON!");
        }
    }

    public static MCTextRoot parse(@NonNull JsonElement json, @NonNull String original) {
        MCTextRoot root = new MCTextRoot(MCTextType.JSON, original);
        doParseJson(root, new TextFormat(), json);
        return root;
    }

    protected static void doParseJson(@NonNull MCTextRoot root, @NonNull TextFormat format, @NonNull JsonElement element) {
        String text = null;
        if (element.isJsonPrimitive()) {
            text = element.getAsString();
        } else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                doParseJson(root, format, child);
            }
            return;
        } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            checkStyle(object, format, TextStyle.BOLD, "bold");
            checkStyle(object, format, TextStyle.ITALIC, "italic");
            checkStyle(object, format, TextStyle.UNDERLINE, "underline");
            checkStyle(object, format, TextStyle.STRIKETHROUGH, "strikethrough");
            format.setTextColor(COLOR_LOOKUP.get(getString(object, "color")));
            JsonElement textElement;
            if (object.has("text") && (textElement = object.get("text")).isJsonPrimitive()) {
                text = textElement.getAsString();
            }
        }
        if (text != null && !text.isEmpty()) {
            root.getChildren().add(new TextComponentString(format, text));
        }
        if (element.isJsonObject()) {
            JsonElement extra;
            if (element.getAsJsonObject().has("extra") && (extra = element.getAsJsonObject().get("extra")).isJsonArray()) {
                for (JsonElement child : extra.getAsJsonArray()) {
                    doParseJson(root, format, child);
                }
            }
        }
    }

    protected static void checkStyle(@NonNull JsonObject object, @NonNull TextFormat format, int mask, @NonNull String name) {
        JsonElement element;
        if (object.has(name) && (element = object.get(name)).isJsonPrimitive() && "true".equals(element.getAsString())) {
            format.setStyle(format.getStyle() | mask);
        } else {
            format.setStyle(format.getStyle() & (~mask));
        }
    }

    protected static String getString(@NonNull JsonObject object, @NonNull String name) {
        JsonElement element;
        return object.has(name) && (element = object.get(name)).isJsonPrimitive() ? element.getAsString() : null;
    }
}
