/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package net.daporkchop.lib.minecraft.text;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.pool.recycler.Recycler;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.logging.console.TextFormat;
import net.daporkchop.lib.logging.format.component.TextComponent;
import net.daporkchop.lib.minecraft.text.component.MCTextRoot;
import net.daporkchop.lib.minecraft.text.format.ChatColor;
import net.daporkchop.lib.minecraft.text.format.ChatFormat;
import net.daporkchop.lib.minecraft.text.format.FormattingCode;

import java.io.IOException;

import static net.daporkchop.lib.logging.format.TextStyle.*;

/**
 * Encodes Minecraft text.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class MCTextEncoder {
    @SneakyThrows(IOException.class)
    public String encode(@NonNull MCTextType type, @NonNull TextComponent component) {
        if (component instanceof MCTextRoot && ((MCTextRoot) component).getType() == type) {
            //return ((MCTextRoot) component).getOriginal();
        }

        Recycler<StringBuilder> recycler = PorkUtil.stringBuilderRecycler();
        StringBuilder builder = recycler.allocate();

        encode(type, builder, component);

        String result = builder.toString();
        recycler.release(builder); //return builder to the recycler
        return result;
    }

    public void encode(@NonNull MCTextType type, @NonNull Appendable dst, @NonNull TextComponent component) throws IOException {
        if (component instanceof MCTextRoot && ((MCTextRoot) component).getType() == type) {
            //dst.append(((MCTextRoot) component).getOriginal());
            //return;
        }

        switch (type) {
            case LEGACY:
                encodeLegacy(dst, component, new TextFormat().setTextColor(ChatColor.WHITE.awtColor()), ChatFormat.RESET);
                break;
            case JSON:
                //TODO
                break;
            default:
                throw new IllegalArgumentException(type.name());
        }
    }

    protected FormattingCode encodeLegacy(@NonNull Appendable dst, @NonNull TextComponent component, @NonNull TextFormat format, @NonNull FormattingCode lastColor) throws IOException {
        lastColor = writeLegacyFormatting(dst, component, format, lastColor);

        for (TextComponent child : component.getChildren()) {
            lastColor = encodeLegacy(dst, child, format, lastColor);
        }
        return lastColor;
    }

    protected FormattingCode writeLegacyFormatting(@NonNull Appendable dst, @NonNull TextComponent component, @NonNull TextFormat format, @NonNull FormattingCode lastColor) throws IOException {
        String text = component.getText();

        FormattingCode newColor = PorkUtil.fallbackIfNull(FormattingCode.closestTo(component.getColor()), lastColor);

        int newStyle = component.getStyle();
        int oldStyle = format.getStyle();

        boolean colorChanged = newColor != lastColor;
        boolean styleRemoved = (~oldStyle & newStyle) != 0;
        boolean rewriteStyle = colorChanged || styleRemoved || (lastColor != ChatFormat.RESET && newStyle != 0);

        if (text != null) {
            if (rewriteStyle) {
                if (colorChanged) {
                    dst.append('§').append(newColor.code());
                }

                if (isBold(newStyle)) {
                    dst.append('§').append('l');
                }
                if (isItalic(newStyle)) {
                    dst.append('§').append('o');
                }
                if (isUnderline(newStyle)) {
                    dst.append('§').append('n');
                }
                if (isStrikethrough(newStyle)) {
                    dst.append('§').append('m');
                }
            }
            dst.append(text);
        }

        if (newColor.isColor()) {
            format.setTextColor(((ChatColor) newColor).awtColor());
        }
        format.setStyle(newStyle);
        return newColor;
    }
}
