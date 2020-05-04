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

package net.daporkchop.lib.minecraft.text.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.function.PFunctions;
import net.daporkchop.lib.common.misc.InstancePool;
import net.daporkchop.lib.logging.console.TextFormat;
import net.daporkchop.lib.logging.format.TextStyle;
import net.daporkchop.lib.logging.format.component.TextComponentString;
import net.daporkchop.lib.minecraft.text.MCTextFormat;
import net.daporkchop.lib.minecraft.text.MCTextType;
import net.daporkchop.lib.minecraft.text.component.MCTextRoot;

import java.awt.Color;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 * @see net.daporkchop.lib.minecraft.text.MCTextType#JSON
 */
@UtilityClass
public class JsonTextParser {
    protected final JsonParser                PARSER       = InstancePool.getInstance(JsonParser.class);
    protected final Map<String, MCTextFormat> COLOR_LOOKUP = Arrays.stream(MCTextFormat.COLORS)
            .collect(Collectors.toMap(MCTextFormat::name, PFunctions.identity()));

    public MCTextRoot parse(@NonNull String raw) {
        try {
            return parse(PARSER.parse(raw), raw);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON!");
        }
    }

    public MCTextRoot parse(@NonNull JsonElement json, @NonNull String original) {
        MCTextRoot root = new MCTextRoot(MCTextType.JSON, original);
        doParseJson(root, new TextFormat(), json);
        return root;
    }

    protected void doParseJson(@NonNull MCTextRoot root, @NonNull TextFormat format, @NonNull JsonElement element) {
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
            format.setTextColor(getColor(getString(object, "color")));
            JsonElement textElement;
            if (object.has("text") && (textElement = object.get("text")).isJsonPrimitive()) {
                text = textElement.getAsString();
            }
        }
        if (text != null && !text.isEmpty()) {
            root.addChild(new TextComponentString(format, text));
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

    protected void checkStyle(@NonNull JsonObject object, @NonNull TextFormat format, int mask, @NonNull String name) {
        JsonElement element;
        if (object.has(name) && (element = object.get(name)).isJsonPrimitive() && "true".equals(element.getAsString())) {
            format.setStyle(format.getStyle() | mask);
        } else {
            format.setStyle(format.getStyle() & (~mask));
        }
    }

    protected String getString(@NonNull JsonObject object, @NonNull String name) {
        JsonElement element;
        return object.has(name) && (element = object.get(name)).isJsonPrimitive() ? element.getAsString() : null;
    }

    protected Color getColor(String name) {
        if (name == null)   {
            return null;
        }
        MCTextFormat format = MCTextFormat.lookupColor(name);
        if (format == null) {
            if ("reset".equalsIgnoreCase(name)) {
                format = MCTextFormat.RESET;
            } else {
                throw new IllegalArgumentException("Unknown color code: \"" + name + '"');
            }
        } else if (!format.hasColor())  {
            throw new IllegalStateException();
        }
        return format.awtColor();
    }
}
