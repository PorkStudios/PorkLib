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

package net.daporkchop.lib.binary.serialization.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.serialization.Serializer;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A {@link Serializer} that uses java's built-in {@link Serializable} interface to read and write
 * objects.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BasicSerializer<T extends Serializable> implements Serializer<T> {
    private static final BasicSerializer INSTANCE = new BasicSerializer();

    /**
     * Get an instance of {@link BasicSerializer}
     *
     * @param <T> the type of object to serialize
     * @return an instance of {@link BasicSerializer} with the requested type
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> BasicSerializer<T> getInstance() {
        return (BasicSerializer<T>) INSTANCE;
    }

    @Override
    public void write(@NonNull T val, @NonNull DataOut out) throws IOException {
        try (ObjectOutputStream oOut = new ObjectOutputStream(out)) {
            oOut.writeObject(val);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T read(@NonNull DataIn in) throws IOException {
        try (ObjectInputStream oIn = new ObjectInputStream(in)) {
            return (T) oIn.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
