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

package net.daporkchop.lib.config.util;

import net.daporkchop.lib.config.attribute.Comment;
import net.daporkchop.lib.reflection.util.Type;

import java.util.List;
import java.util.Map;

/**
 * Common interface for unifying parsers for different config formats
 *
 * @author DaPorkchop_
 */
public interface Element<V> {
    /**
     * Gets the type of data stored in the element
     */
    Type getType();

    /**
     * Gets the value stored in this element
     */
    V getValue();

    /**
     * Gets the name of this element
     */
    String getName();

    /**
     * Gets the element that contains this element
     *
     * This may return either a {@link ContainerElement} or a {@link ArrayElement}, or {@code null} if
     * this element is the root element.
     */
    Element getParent();

    /**
     * Gets the comment on this element
     */
    Comment getComment();

    /**
     * Checks if this element is the root element in the config tree.
     */
    default boolean isRoot()    {
        return this.getParent() == null;
    }

    default boolean booleanValue()  {
        return (Boolean) this.getValue();
    }

    default byte byteValue()  {
        return (Byte) this.getValue();
    }

    default short shortValue()  {
        return (Short) this.getValue();
    }

    default int intValue()  {
        return (Integer) this.getValue();
    }

    default long longValue()  {
        return (Long) this.getValue();
    }

    default float floatValue()  {
        return (Float) this.getValue();
    }

    default double doubleValue()  {
        return (Double) this.getValue();
    }

    default char charValue()  {
        return (Character) this.getValue();
    }

    default boolean isString()  {
        return this.getValue() instanceof String;
    }

    default String stringValue()  {
        return (String) this.getValue();
    }

    interface ContainerElement extends Element<Map<String, Element>> {
    }

    interface ArrayElement extends Element<List<Element>>   {
    }
}
