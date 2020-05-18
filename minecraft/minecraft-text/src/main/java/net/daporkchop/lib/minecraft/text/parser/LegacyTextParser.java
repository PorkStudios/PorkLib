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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.logging.console.TextFormat;
import net.daporkchop.lib.logging.format.TextStyle;
import net.daporkchop.lib.logging.format.component.TextComponentString;
import net.daporkchop.lib.minecraft.text.MCTextFormat;
import net.daporkchop.lib.minecraft.text.MCTextType;
import net.daporkchop.lib.minecraft.text.component.MCTextRoot;

import java.awt.Color;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * @author DaPorkchop_
 * @see net.daporkchop.lib.minecraft.text.MCTextType#LEGACY
 */
@UtilityClass
public class LegacyTextParser {
    public MCTextRoot parse(@NonNull String raw) {
        try {
            return parse(new StringReader(raw), raw);
        } catch (IOException e) {
            //impossible
            throw new IllegalStateException(e);
        }
    }

    public MCTextRoot parse(@NonNull Reader reader) throws IOException {
        return parse(reader, null);
    }

    protected MCTextRoot parse(@NonNull Reader reader, String raw) throws IOException {
        MCTextRoot root = new MCTextRoot(MCTextType.LEGACY, raw);

        TextFormat format = new TextFormat();
        boolean expectingCode = false;
        try (Handle<StringBuilder> handle = PorkUtil.STRINGBUILDER_POOL.get()) {
            StringBuilder builder = handle.get();
            builder.setLength(0);

            int nextChar;
            while ((nextChar = reader.read()) != -1)    {
                if (expectingCode)  {
                    MCTextFormat code = MCTextFormat.lookup((char) nextChar);
                    if (code == null)   {
                        throw new IllegalArgumentException(String.format("Invalid formatting code: %c", (char) nextChar));
                    }

                    if (code.hasColor())    {
                        format.setTextColor(code.awtColor()).setStyle(0);
                    } else {
                        switch (code)   {
                            case BOLD:
                                format.setStyle(format.getStyle() | TextStyle.BOLD);
                                break;
                            case STRIKETHROUGH:
                                format.setStyle(format.getStyle() | TextStyle.STRIKETHROUGH);
                                break;
                            case UNDERLINE:
                                format.setStyle(format.getStyle() | TextStyle.UNDERLINE);
                                break;
                            case ITALIC:
                                format.setStyle(format.getStyle() | TextStyle.ITALIC);
                                break;
                            case RESET:
                                format.setStyle(0).setTextColor(null);
                                break;
                        }
                    }
                    expectingCode = false;
                } else if (nextChar == '§') {
                    createComponent(root, builder, format);
                    expectingCode = true;
                } else {
                    builder.append((char) nextChar);
                }
            }
            createComponent(root, builder, format);
        }
        return root;
    }

    protected void createComponent(@NonNull MCTextRoot root, @NonNull StringBuilder builder, @NonNull TextFormat format)   {
        if (builder.length() > 0)    {
            root.addChild(new TextComponentString(format, builder.toString()));
            builder.setLength(0);
        }
    }
}
