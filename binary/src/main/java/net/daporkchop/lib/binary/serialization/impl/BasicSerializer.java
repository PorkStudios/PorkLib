/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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
public class BasicSerializer<T extends Serializable> implements Serializer<T> {
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
