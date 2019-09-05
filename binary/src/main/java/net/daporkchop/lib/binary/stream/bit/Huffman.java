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

package net.daporkchop.lib.binary.stream.bit;

import lombok.NonNull;
import net.daporkchop.lib.math.primitive.BinMath;
import net.daporkchop.lib.binary.util.map.CharacterBooleanHashMap;
import net.daporkchop.lib.binary.util.map.CharacterBooleanMap;
import net.daporkchop.lib.binary.util.map.CharacterIntegerHashMap;
import net.daporkchop.lib.binary.util.map.CharacterIntegerMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An implementation of Huffman text compression in Java
 *
 * @author DaPorkchop_
 */
public class Huffman {
    public static void write(@NonNull OutputStream stream, @NonNull String text) {
        try {
            BitOutputStream bitStream = new BitOutputStream(stream);
            write(bitStream, text.toCharArray());
            bitStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(@NonNull OutputStream stream, @NonNull char[] text) {
        try {
            BitOutputStream bitStream = new BitOutputStream(stream);
            write(bitStream, text);
            bitStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(@NonNull BitOutputStream stream, @NonNull String text) {
        write(stream, text.toCharArray());
    }

    public static void write(@NonNull BitOutputStream stream, @NonNull char[] text) {
        try {
            //find all letters in text
            CharacterBooleanMap map = new CharacterBooleanHashMap();
            for (char c : text) {
                map.put(c, true);
            }
            int requiredBits = BinMath.getNumBitsNeededFor(map.getSize());

            //map each letter to an index and write index
            CharacterIntegerMap indexes = new CharacterIntegerHashMap();
            AtomicInteger i = new AtomicInteger(0);
            {
                StringBuilder builder = new StringBuilder();
                map.forEachKey(c -> {
                    int j = i.getAndIncrement();
                    indexes.put(c, j);
                    builder.append(c);
                });
                byte[] index = builder.toString().getBytes(StandardCharsets.UTF_8);
                stream.writeLength(index.length);
                stream.write(index);
            }

            //write values
            stream.writeLength(text.length);
            for (char c : text) {
                stream.writeBits(requiredBits, indexes.get(c));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readToString(@NonNull InputStream stream) {
        BitInputStream bitStream = new BitInputStream(stream);
        char[] letters = read(bitStream);
        return new String(letters);
    }

    public static char[] read(@NonNull InputStream stream) {
        BitInputStream bitStream = new BitInputStream(stream);
        return read(bitStream);
    }

    public static String readToString(@NonNull BitInputStream stream) {
        return new String(read(stream));
    }

    public static char[] read(@NonNull BitInputStream stream) {
        try {
            char[] index;
            {
                int indexByteLength = stream.readLength();
                byte[] b = new byte[indexByteLength];
                stream.read(b);
                String s = new String(b, StandardCharsets.UTF_8);
                index = s.toCharArray();
            }
            int indexBits = BinMath.getNumBitsNeededFor(index.length);

            int len = stream.readLength();
            char[] text = new char[len];
            for (int i = 0; i < text.length; i++) {
                int j = stream.readBits(indexBits);
                text[i] = index[j];
            }
            return text;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
