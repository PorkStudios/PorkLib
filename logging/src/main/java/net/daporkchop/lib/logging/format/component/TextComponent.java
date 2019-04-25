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

package net.daporkchop.lib.logging.format.component;

import lombok.NonNull;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

/**
 * Base component in a formatted text string.
 *
 * @author DaPorkchop_
 */
public interface TextComponent {
    /**
     * Gets this component's text (and the text of all children, if present).
     *
     * @return the raw text
     */
    default String toRawString() {
        StringBuilder builder = new StringBuilder(); //TODO: pool these
        this.internal_toRawStringRecursive(builder);
        return builder.toString();
    }

    /**
     * Gets this component's text.
     * <p>
     * This method may return {@code null} if it does not contain any text itself.
     * <p>
     * If this component has both children and text, it's own text should be applied before any children.
     *
     * @return this component's text
     */
    String getText();

    /**
     * Gets a list containing all child elements of this component.
     * <p>
     * This method may never return {@code null}, and should return an empty list (e.g. via {@link Collections#emptyList()}) if it does not contain any
     * children.
     *
     * @return all children of this text component
     */
    List<TextComponent> getChildren();

    /**
     * Gets this text component's color, if set. If no color is explicitly set (i.e. the default color should be used), this method returns {@code null}.
     *
     * @return this text component's color
     */
    Color getColor();

    /**
     * Gets this text component's background color, if set. If no color is explicitly set (i.e. the default background color should be used), this method
     * returns {@code null}.
     *
     * @return this text component's background color
     */
    Color getBackgroundColor();

    /**
     * Gets this text component's text style.
     *
     * @return this text component's text style
     * @see net.daporkchop.lib.logging.format.TextStyle
     */
    int getStyle();

    default TextComponent insertToHeadOf(@NonNull TextComponent component)  {
        if (this.getText() == null) {
            return component;
        } else if (component.getText() == null) {
            component.getChildren().add(0, this);
            return component;
        } else {
            return new TextComponentHolder(this, component);
        }
    }

    default void internal_toRawStringRecursive(@NonNull StringBuilder builder) {
        {
            String text = this.getText();
            if (text != null) {
                builder.append(text);
            }
        }
        for (TextComponent child : this.getChildren()) {
            child.internal_toRawStringRecursive(builder);
        }
    }
}
