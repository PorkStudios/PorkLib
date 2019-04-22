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

package net.daporkchop.lib.minecraft.text.util;

import com.google.gson.JsonParser;
import lombok.NonNull;
import net.daporkchop.lib.minecraft.text.ITextComponent;

import java.nio.CharBuffer;

public class ChatUtils {
    private static final JsonParser parser = new JsonParser();

    public static String getOldText(@NonNull String json) {
        ITextComponent component = ITextComponent.Serializer.jsonToComponent(json.trim());
        String text = component.getFormattedText();
        if (text.startsWith("{")) {
            text = ITextComponent.Serializer.jsonToComponent(text).getFormattedText();
        }
        return text;
    }

    public static String toHTML(@NonNull String legacy) {
        StringBuilder builder = new StringBuilder();
        builder.append("<span>");
        htmlifyRecursive(builder, CharBuffer.wrap(legacy));
        return builder.toString();
    }

    protected static void htmlifyRecursive(@NonNull StringBuilder builder, @NonNull CharBuffer buffer)  {
        MAINLOOP:
        while (buffer.hasRemaining())   {
            char c = buffer.get();
            if (c == '§')   {
                if (buffer.hasRemaining())  {
                    String style = null;
                    switch (buffer.get())   {
                        case '0':
                            style = "color: #000000";
                            break;
                        case '1':
                            style = "color: #0000AA";
                            break;
                        case '2':
                            style = "color: #00AA00";
                            break;
                        case '3':
                            style = "color: #00AAAA";
                            break;
                        case '4':
                            style = "color: #AA0000";
                            break;
                        case '5':
                            style = "color: #AA00AA";
                            break;
                        case '6':
                            style = "color: #FFAA00";
                            break;
                        case '7':
                            style = "color: #AAAAAA";
                            break;
                        case '8':
                            style = "color: #555555";
                            break;
                        case '9':
                            style = "color: #5555FF";
                            break;
                        case 'a':
                            style = "color: #55FF55";
                            break;
                        case 'b':
                            style = "color: #55FFFF";
                            break;
                        case 'c':
                            style = "color: #FF5555";
                            break;
                        case 'd':
                            style = "color: #FF55FF";
                            break;
                        case 'e':
                            style = "color: #FFFF55";
                            break;
                        case 'f':
                            style = "color: #FFFFFF";
                            break;
                        case 'l':
                            style = "font-weight: bold";
                            break;
                        case 'm':
                            style = "font-decoration: line-through";
                            break;
                        case 'n':
                            style = "font-decoration: underline";
                            break;
                        case 'o':
                            style = "font-style: italic";
                            break;
                        case 'r':
                            break MAINLOOP;
                        default:
                            throw new IllegalStateException();
                    }
                    buffer.append(String.format("<span style=\"%s\">", style));
                    htmlifyRecursive(builder, buffer);
                }
            }
        }
        builder.append("</span>");
    }
}
