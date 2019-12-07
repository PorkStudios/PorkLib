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
public class MinecraftFormatParser implements FormatParser {
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
