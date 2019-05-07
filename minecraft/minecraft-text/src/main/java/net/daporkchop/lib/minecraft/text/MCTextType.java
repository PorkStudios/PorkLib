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

package net.daporkchop.lib.minecraft.text;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.logging.format.component.TextComponent;
import net.daporkchop.lib.minecraft.text.component.MCTextRoot;
import net.daporkchop.lib.minecraft.text.parser.JsonTextParser;
import net.daporkchop.lib.minecraft.text.parser.LegacyTextParser;

import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public enum MCTextType {
    /**
     * The new JSON chat formatting system as described at https://wiki.vg/Chat.
     */
    JSON(JsonTextParser::parse),
    /**
     * The legacy text formatting system using text interleaved with formatting codes (indicated by '§'), as described at
     * https://minecraft.gamepedia.com/Formatting_codes.
     */
    LEGACY(LegacyTextParser::parse),
    ;

    @NonNull
    protected final Function<String, MCTextRoot> parser;
}
