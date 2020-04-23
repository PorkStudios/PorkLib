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

package net.daporkchop.lib.config.decoder;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.misc.string.PUnsafeStrings;
import net.daporkchop.lib.config.attribute.Comment;
import net.daporkchop.lib.config.util.Element;
import net.daporkchop.lib.reflection.util.Type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A simple config system based on the one used by Minecraft Forge
 *
 * @author DaPorkchop_
 */
public class PorkConfigDecoder implements ConfigDecoder {
    @Override
    public Element.ContainerElement decode(@NonNull DataIn in) throws IOException {
        Element.ContainerElement root = Element.dummyContainer(null, null, null);
        this.decodeInto(root, new BufferedReader(new InputStreamReader(in.asInputStream())));
        return root;
    }

    protected void decodeInto(@NonNull Element.ContainerElement container, @NonNull BufferedReader reader) throws IOException   {
        List<String> commentBuf = new LinkedList<>();
        String next;
        while (true) {
            next = reader.readLine();
            if (next == null)   {
                return;
            } else {
                next = next.trim();
            }
            if (next.isEmpty()) {
                continue;
            }
            switch (next.charAt(0)) {
                case '#': {
                    //load comment and continue
                    commentBuf.add(next.substring(1, next.length()));
                }
                break;
                case 'O': {
                    this.parseVal(container, reader, next, Type.OBJECT, commentBuf);
                }
                break;
                case 'Z': {
                    this.parseVal(container, reader, next, Type.BOOLEAN, commentBuf);
                }
                break;
                case 'B': {
                    this.parseVal(container, reader, next, Type.BYTE, commentBuf);
                }
                break;
                case 'T': {
                    this.parseVal(container, reader, next, Type.SHORT, commentBuf);
                }
                break;
                case 'I': {
                    this.parseVal(container, reader, next, Type.INT, commentBuf);
                }
                break;
                case 'L': {
                    this.parseVal(container, reader, next, Type.LONG, commentBuf);
                }
                break;
                case 'F': {
                    this.parseVal(container, reader, next, Type.FLOAT, commentBuf);
                }
                break;
                case 'D': {
                    this.parseVal(container, reader, next, Type.DOUBLE, commentBuf);
                }
                break;
                case 'C': {
                    this.parseVal(container, reader, next, Type.CHAR, commentBuf);
                }
                break;
                case 'S': {
                    this.parseVal(container, reader, next, Type.STRING, commentBuf);
                }
                break;
                case 'o': {
                    this.parseVal(container, reader, next, Type.ARRAY_OBJECT, commentBuf);
                }
                break;
                case 'z': {
                    this.parseVal(container, reader, next, Type.ARRAY_BOOLEAN, commentBuf);
                }
                break;
                case 'b': {
                    this.parseVal(container, reader, next, Type.ARRAY_BYTE, commentBuf);
                }
                break;
                case 't': {
                    this.parseVal(container, reader, next, Type.ARRAY_SHORT, commentBuf);
                }
                break;
                case 'i': {
                    this.parseVal(container, reader, next, Type.ARRAY_INT, commentBuf);
                }
                break;
                case 'l': {
                    this.parseVal(container, reader, next, Type.ARRAY_LONG, commentBuf);
                }
                break;
                case 'f': {
                    this.parseVal(container, reader, next, Type.ARRAY_FLOAT, commentBuf);
                }
                break;
                case 'd': {
                    this.parseVal(container, reader, next, Type.ARRAY_DOUBLE, commentBuf);
                }
                break;
                case 'c': {
                    this.parseVal(container, reader, next, Type.ARRAY_CHAR, commentBuf);
                }
                break;
                case '}': {
                    //end of object
                    return;
                }
                default: {
                    throw new IllegalStateException(String.format("Found illegal character: %c", next.charAt(0)));
                }
            }
        }
    }

    protected void parseVal(@NonNull Element.ContainerElement container, @NonNull BufferedReader reader, @NonNull String line, @NonNull Type expectedType, @NonNull List<String> comments) throws IOException    {
        try {
            if (line.charAt(1) != ':') {
                throw new IllegalStateException(String.format("Invalid char: %c", line.charAt(1)));
            }
            int equals = line.indexOf('=', 2);
            if (equals == -1) {
                throw new IllegalStateException("No value set!");
            }
            String name = line.substring(2, equals).trim();
            String valueText = line.substring(equals + 1, line.length()).trim();
            if (valueText.isEmpty()) {
                throw new IllegalStateException("No value set!");
            }
            Object val = null;
            switch (expectedType) {
                case OBJECT: {
                    if (line.charAt(line.length() - 1) != '{') {
                        throw new IllegalStateException("Not an object!");
                    }
                    Element.ContainerElement subContainer = Element.dummyContainer(name, container, Comment.from(comments.toArray(new String[comments.size()])));
                    this.decodeInto(subContainer, reader);
                    container.setElement(name, subContainer);
                }
                return;
                case BOOLEAN: {
                    val = Boolean.parseBoolean(valueText);
                }
                break;
                case BYTE: {
                    val = Byte.parseByte(valueText);
                }
                break;
                case SHORT: {
                    val = Short.parseShort(valueText);
                }
                break;
                case INT: {
                    val = Integer.parseInt(valueText);
                }
                break;
                case LONG: {
                    val = Long.parseLong(valueText);
                }
                break;
                case FLOAT: {
                    val = Float.parseFloat(valueText);
                }
                break;
                case DOUBLE: {
                    val = Double.parseDouble(valueText);
                }
                break;
                case CHAR: {
                    val = valueText.charAt(0);
                }
                break;
                case STRING: {
                    val = valueText;
                }
                break;
                default: {
                    throw new UnsupportedOperationException(expectedType.name());
                }
            }
            container.setElement(name, Element.dummyElement(name, val, expectedType, container, Comment.from(comments.toArray(new String[comments.size()]))));
        } finally {
            comments.clear();
        }
    }

    @Override
    public void encode(@NonNull Element.ContainerElement root, @NonNull DataOut out) throws IOException {
        try (PrintStream stream = new PrintStream(DataOut.wrapNonClosing(out))) {
            this.encodeRecursive(root, stream, 0);
        }
    }

    protected void encodeRecursive(@NonNull Element.ContainerElement container, @NonNull PrintStream out, int depth) throws IOException  {
        String indent = this.indent(depth);
        boolean previousHadComment = false;
        for (Map.Entry<String, Element> entry : container.getValue().entrySet())  {
            if (previousHadComment) {
                out.println();
            }
            if (previousHadComment = entry.getValue().getComment().isPresent())  {
                for (String line : entry.getValue().getComment().getCommentLines())    {
                    out.printf("%s# %s", indent, line);
                    out.println();
                }
            }
            switch (entry.getValue().getType()) {
                case OBJECT: {
                    out.printf("%sO:%s={", indent, entry.getKey());
                    out.println();
                    this.encodeRecursive((Element.ContainerElement) entry.getValue(), out, depth + 1);
                }
                break;
                case BOOLEAN: {
                    out.printf("%sZ:%s=%b", indent, entry.getKey(), entry.getValue().booleanValue());
                    out.println();
                }
                break;
                case BYTE: {
                    out.printf("%sB:%s=%d", indent, entry.getKey(), entry.getValue().byteValue());
                    out.println();
                }
                break;
                case SHORT: {
                    out.printf("%sT:%s=%d", indent, entry.getKey(), entry.getValue().shortValue());
                    out.println();
                }
                break;
                case INT: {
                    out.printf("%sI:%s=%d", indent, entry.getKey(), entry.getValue().intValue());
                    out.println();
                }
                break;
                case LONG: {
                    out.printf("%sL:%s=%d", indent, entry.getKey(), entry.getValue().longValue());
                    out.println();
                }
                break;
                case FLOAT: {
                    out.printf("%sF:%s=%s", indent, entry.getKey(), String.valueOf(entry.getValue().floatValue()).replace(',', '.'));
                    out.println();
                }
                break;
                case DOUBLE: {
                    out.printf("%sD:%s=%s", indent, entry.getKey(), String.valueOf(entry.getValue().doubleValue()).replace(',', '.'));
                    out.println();
                }
                break;
                case CHAR: {
                    out.printf("%sC:%s=%c", indent, entry.getKey(), entry.getValue().charValue());
                    out.println();
                }
                break;
                case STRING: {
                    out.printf("%sS:%s=%s", indent, entry.getKey(), entry.getValue().stringValue());
                    out.println();
                }
                break;
                default: {
                    throw new UnsupportedOperationException(entry.getValue().getType().name());
                }
            }
        }
        if (depth > 0) {
            out.printf("%s}", this.indent(depth - 1));
            out.println();
        }
    }
    
    protected String indent(int depth)  {
        char[] c = new char[Math.max(0, depth) * 4];
        Arrays.fill(c, ' ');
        return PUnsafeStrings.wrap(c);
    }
}
