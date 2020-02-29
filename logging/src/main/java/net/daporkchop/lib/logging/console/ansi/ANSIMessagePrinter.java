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
public class ANSIMessagePrinter implements MessagePrinter, ANSI {
    @NonNull
    protected final PrintStream printer;

    public ANSIMessagePrinter()    {
        this(DefaultLogger.stdOut);
    }

    @Override
    public void accept(@NonNull TextComponent component) {
        StringBuilder builder = new StringBuilder(); //TODO: pool these
        this.doBuild(builder, component);
        this.printer.println(builder.append(ESC).append("[0m"));
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
