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
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import net.daporkchop.lib.common.misc.InstancePool;
import net.daporkchop.lib.logging.format.FormatParser;
import net.daporkchop.lib.minecraft.text.MCTextType;
import net.daporkchop.lib.minecraft.text.component.MCTextRoot;

/**
 * Parses Minecraft text into formatted components.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public final class MinecraftFormatParser implements FormatParser {
    /**
     * @return a default instance of {@link MinecraftFormatParser} which can parse both legacy and json-formatted text and automatically choose the best
     */
    public static MinecraftFormatParser getDefaultInstance()    {
        return InstancePool.getInstance(MinecraftFormatParser.class);
    }

    /**
     * The type of text that will be parsed by this instance.
     * <p>
     * If {@code null}, this will automatically detect the type based on input data and parse it accordingly.
     */
    protected MCTextType type;

    @Override
    public MCTextRoot parse(@NonNull String text) {
        if (this.type == null) {
            //autodetect
            JsonElement element = null;
            try {
                element = InstancePool.getInstance(JsonParser.class).parse(text);
            } catch (JsonSyntaxException e) {
                //not a json string, treat as legacy
                return LegacyTextParser.parse(text);
            }
            return JsonTextParser.parse(element, text);
        } else {
            return this.type.getParser().apply(text);
        }
    }
}
