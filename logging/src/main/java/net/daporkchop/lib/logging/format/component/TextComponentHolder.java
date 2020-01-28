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

package net.daporkchop.lib.logging.format.component;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author DaPorkchop_
 */
@Getter
public class TextComponentHolder implements TextComponent {
    @NonNull
    protected List<TextComponent> children;

    public TextComponentHolder() {
        this(Collections.emptyList());
    }

    public TextComponentHolder(@NonNull TextComponent... children) {
        this(new ArrayList<>());

        for (TextComponent child : children) {
            if (child == null) {
                throw new NullPointerException();
            }
            this.children.add(child);
        }
    }

    public TextComponentHolder(@NonNull List<TextComponent> children) {
        this.children = children;

        if (children.contains(null))   {
            throw new NullPointerException();
        }
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public List<TextComponent> getChildren()    {
        List<TextComponent> children = this.children;
        return children == Collections.<TextComponent>emptyList() ? children : Collections.unmodifiableList(this.children);
    }

    @Override
    public synchronized void addChild(@NonNull TextComponent child) {
        List<TextComponent> children = this.children;
        if (children == Collections.<TextComponent>emptyList()) {
            this.children = children = new ArrayList<>();
        }
        children.add(child);
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
}
