/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.logging.console.TextFormat;
import net.daporkchop.lib.logging.format.component.TextComponent;
import net.daporkchop.lib.minecraft.text.component.MCTextRoot;

import java.awt.Color;
import java.io.IOException;

import static net.daporkchop.lib.logging.format.TextStyle.*;

/**
 * Encodes Minecraft text.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class MCTextEncoder {
    public String encode(@NonNull MCTextType type, @NonNull TextComponent component) {
        if (component instanceof MCTextRoot && ((MCTextRoot) component).getType() == type) {
            //return ((MCTextRoot) component).getOriginal();
        }

        try (Handle<StringBuilder> handle = PorkUtil.STRINGBUILDER_POOL.get()) {
            StringBuilder builder = handle.value();
            builder.setLength(0);
            encode(type, builder, component);
            return builder.toString();
        } catch (IOException e) {
            //impossible
            throw new IllegalStateException(e);
        }
    }

    public void encode(@NonNull MCTextType type, @NonNull Appendable dst, @NonNull TextComponent component) throws IOException {
        if (component instanceof MCTextRoot && ((MCTextRoot) component).getType() == type) {
            //dst.append(((MCTextRoot) component).getOriginal());
            //return;
        }

        switch (type) {
            case LEGACY:
                encodeLegacy(dst, component, new TextFormat().setTextColor(MCTextFormat.WHITE.awtColor()), MCTextFormat.RESET);
                break;
            case JSON:
                //TODO
                break;
            default:
                throw new IllegalArgumentException(type.name());
        }
    }

    protected MCTextFormat encodeLegacy(@NonNull Appendable dst, @NonNull TextComponent component, @NonNull TextFormat format, @NonNull MCTextFormat lastColor) throws IOException {
        lastColor = writeLegacyFormatting(dst, component, format, lastColor);

        for (TextComponent child : component.getChildren()) {
            lastColor = encodeLegacy(dst, child, format, lastColor);
        }
        return lastColor;
    }

    protected MCTextFormat writeLegacyFormatting(@NonNull Appendable dst, @NonNull TextComponent component, @NonNull TextFormat format, @NonNull MCTextFormat lastColor) throws IOException {
        String text = component.getText();

        MCTextFormat newColor = PorkUtil.fallbackIfNull(MCTextFormat.closestTo(component.getColor()), lastColor);

        int newStyle = component.getStyle();
        int oldStyle = format.getStyle();

        boolean colorChanged = newColor != lastColor;
        boolean styleRemoved = (~oldStyle & newStyle) != 0;
        boolean rewriteStyle = colorChanged || styleRemoved || (lastColor != MCTextFormat.RESET && newStyle != 0);

        if (text != null) {
            if (rewriteStyle) {
                if (colorChanged) {
                    dst.append('§').append(newColor.code());
                }

                if (isBold(newStyle))   {
                    dst.append('§').append('l');
                }
                if (isItalic(newStyle))   {
                    dst.append('§').append('o');
                }
                if (isUnderline(newStyle))   {
                    dst.append('§').append('n');
                }
                if (isStrikethrough(newStyle))   {
                    dst.append('§').append('m');
                }
            }
            dst.append(text);
        }

        format.setTextColor(newColor.awtColor());
        format.setStyle(newStyle);
        return newColor;
    }
}
