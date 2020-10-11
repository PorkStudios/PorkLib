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

package net.daporkchop.lib.logging.format.component;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of an empty {@link TextComponent}.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmptyTextComponent implements TextComponent {
    public static final EmptyTextComponent INSTANCE = new EmptyTextComponent();

    @Override
    public String getText() {
        return "";
    }

    @Override
    public List<TextComponent> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public void pushChild(@NonNull TextComponent child) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TextComponent popChild() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public Color getBackgroundColor() {
        return null;
    }

    @Override
    public int getStyle() {
        return 0;
    }

    @Override
    public String toRawString() {
        return "";
    }

    @Override
    public boolean hasNewline() {
        return false;
    }

    @Override
    public List<TextComponent> splitOnNewlines() {
        return Collections.singletonList(this);
    }
}
