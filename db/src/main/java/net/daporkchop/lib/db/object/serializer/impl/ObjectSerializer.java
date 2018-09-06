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

package net.daporkchop.lib.db.object.serializer.impl;

import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.db.object.serializer.ValueSerializer;
import net.daporkchop.lib.db.util.exception.WrappedException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Allows easy reading and writing of a {@link Serializable}
 *
 * @author DaPorkchop_
 */
public class ObjectSerializer<T extends Serializable> extends ValueSerializer<T> {
    @Override
    public void write(T value, DataOut out) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            oos.close();
            out.writeBytesSimple(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrappedException("Unable to write Object " + value, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T read(DataIn in) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(in.readBytesSimple());
            ObjectInputStream ois = new ObjectInputStream(bais);
            T object = (T) ois.readObject();
            ois.close();
            return object;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new WrappedException("Unable to read Object", e);
        }
    }
}
