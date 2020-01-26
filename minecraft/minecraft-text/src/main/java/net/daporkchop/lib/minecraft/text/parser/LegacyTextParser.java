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

package net.daporkchop.lib.minecraft.text.parser;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
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
        MCTextRoot root = new MCTextRoot(MCTextType.LEGACY, raw);

        TextFormat format = new TextFormat();
        boolean expectingCode = false;
        try (Reader reader = new StringReader(raw)) {
            StringBuffer buffer = new StringBuffer(); //TODO: pool these
            int nextChar;
            while ((nextChar = reader.read()) != -1)    {
                if (expectingCode)  {
                    MCTextFormat code = MCTextFormat.lookup((char) nextChar);
                    if (code == null)   {
                        throw new IllegalArgumentException(String.format("Invalid formatting code: %c", (char) nextChar));
                    }
                    format.setTextColor(code.awtColor());

                    if (code.hasColor())    {
                        format.setStyle(0);
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
                    createComponent(root, buffer, format);
                    expectingCode = true;
                } else {
                    buffer.append((char) nextChar);
                }
            }
            createComponent(root, buffer, format);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return root;
    }

    protected void createComponent(@NonNull MCTextRoot root, @NonNull StringBuffer buffer, @NonNull TextFormat format)   {
        if (buffer.length() > 0)    {
            root.getChildren().add(new TextComponentString(format, buffer.toString()));
            buffer.setLength(0);
        }
    }
}
