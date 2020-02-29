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

package net.daporkchop.lib.gui.component.type.functional;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.state.functional.TextBoxState;

import java.awt.*;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public interface TextBox extends Component<TextBox, TextBoxState> {
    @Override
    default TextBoxState getState() {
        return this.isVisible() ?
                this.isEnabled() ?
                        this.isHovered() ? TextBoxState.ENABLED_HOVERED : TextBoxState.ENABLED
                        : this.isHovered() ? TextBoxState.DISABLED_HOVERED : TextBoxState.DISABLED
                : TextBoxState.HIDDEN;
    }

    String getText();
    TextBox setText(@NonNull String text);

    String getHint();
    TextBox setHint(@NonNull String hint);
    int getHintColor();
    TextBox setHintColor(int argb);
    default TextBox setHintColor(@NonNull Color color)  {
        return this.setHintColor(color.getRGB());
    }

    boolean isPassword();

    TextBox addTextChangedListener(@NonNull String name, @NonNull Consumer<String> callback);
    TextBox removeTextChangedListener(@NonNull String name);

    default TextBox addTextChangedListener(@NonNull Consumer<String> callback)   {
        return this.addTextChangedListener(String.format("%s@%d", callback.getClass().getCanonicalName(), System.identityHashCode(callback)), callback);
    }
}
