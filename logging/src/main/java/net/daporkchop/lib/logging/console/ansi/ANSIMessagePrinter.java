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

package net.daporkchop.lib.logging.console.ansi;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.logging.format.MessageFormatter;
import net.daporkchop.lib.logging.format.MessagePrinter;
import net.daporkchop.lib.logging.format.component.TextComponent;
import net.daporkchop.lib.logging.impl.DefaultLogger;

import java.io.PrintStream;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class ANSIMessagePrinter implements MessagePrinter {
    @NonNull
    protected final PrintStream printer;

    public ANSIMessagePrinter()    {
        this(DefaultLogger.stdOut);
    }

    @Override
    public void accept(@NonNull TextComponent component) {
        StringBuilder builder = new StringBuilder(); //TODO: pool these
        this.doBuild(builder, component);
        this.printer.println(builder);
    }

    protected void doBuild(@NonNull StringBuilder builder, @NonNull TextComponent component)    {
        {
            String text = component.getText();
            if (text != null && !text.isEmpty()) {
                builder.append(ANSIConsole.getUpdateTextFormatCommand(
                        VGAColor.closestTo(component.getColor()),
                        VGAColor.closestTo(component.getBackgroundColor()),
                        component.getStyle()
                )).append(text);
            }
        }
        for (TextComponent child : component.getChildren()) {
            this.doBuild(builder, child);
        }
    }
}
