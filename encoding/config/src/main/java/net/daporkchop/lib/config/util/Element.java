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

package net.daporkchop.lib.config.util;

import com.google.gson.internal.LinkedTreeMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.lib.config.attribute.Comment;
import net.daporkchop.lib.reflection.util.Type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Common interface for unifying parsers for different config formats
 *
 * @author DaPorkchop_
 */
public interface Element<V> {
    static ContainerElement dummyContainer(String nameIn, Element parentIn, Comment commentIn) {
        return new ContainerElement() {
            @Getter
            private final Map<String, Element> value = new LinkedTreeMap<>();
            @Getter
            private final String name = nameIn;
            @Getter
            private final Element parent = parentIn;
            @Getter
            @Setter
            private Comment comment = commentIn;

            @Override
            public Type getType() {
                return Type.OBJECT;
            }
        };
    }

    static <V> Element<V> dummyElement(String nameIn, V valueIn, @NonNull Type typeIn, Element parentIn, Comment commentIn) {
        return new Element<V>() {
            @Getter
            private final V value = valueIn;
            @Getter
            private final String name = nameIn;
            @Getter
            private final Type type = typeIn;
            @Getter
            private final Element parent = parentIn;
            @Getter
            @Setter
            private Comment comment = commentIn;
        };
    }

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
     * <p>
     * This may return either a {@link ContainerElement} or a {@link ArrayElement}, or {@code null} if
     * this element is the root element.
     */
    Element getParent();

    /**
     * Gets the comment on this element
     */
    Comment getComment();

    /**
     * Sets this element's comment
     *
     * @param comment the new comment to set
     */
    void setComment(Comment comment);

    /**
     * Checks if this element has a comment
     */
    default boolean hasComment() {
        return this.getComment() != null && this.getComment().isPresent() && !this.isRoot();
    }

    /**
     * Checks if this element is the root element in the config tree.
     */
    default boolean isRoot() {
        return this.getParent() == null;
    }

    default boolean booleanValue() {
        return (Boolean) this.getValue();
    }

    default byte byteValue() {
        return (Byte) this.getValue();
    }

    default short shortValue() {
        return (Short) this.getValue();
    }

    default int intValue() {
        return (Integer) this.getValue();
    }

    default long longValue() {
        return (Long) this.getValue();
    }

    default float floatValue() {
        return (Float) this.getValue();
    }

    default double doubleValue() {
        return (Double) this.getValue();
    }

    default char charValue() {
        return (Character) this.getValue();
    }

    default boolean isString() {
        return this.getValue() instanceof String;
    }

    default String stringValue() {
        return (String) this.getValue();
    }

    default BigInteger bigIntegerValue() {
        return (BigInteger) this.getValue();
    }

    default BigDecimal bigDecimalValue() {
        return (BigDecimal) this.getValue();
    }

    @SuppressWarnings("unchecked")
    default <T extends Element> T getAs() {
        return (T) this;
    }

    interface ContainerElement extends Element<Map<String, Element>> {
        default Element getElement(@NonNull String name) {
            return this.getValue().get(name);
        }

        default void setElement(@NonNull String name, @NonNull Element element) {
            this.getValue().put(name, element);
        }

        default boolean hasElement(@NonNull String name) {
            return this.getValue().containsKey(name);
        }

        default void removeElement(@NonNull String name) {
            this.getValue().remove(name);
        }
    }

    interface ArrayElement extends Element<List<Element>> {
        default Element getElement(int i) {
            return this.getValue().get(i);
        }

        default int getSize() {
            return this.getValue().size();
        }

        default void add(@NonNull Element element) {
            this.getValue().add(element);
        }
    }
}
