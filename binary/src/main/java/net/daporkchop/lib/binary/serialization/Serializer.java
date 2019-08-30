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

package net.daporkchop.lib.binary.serialization;

import lombok.NonNull;
import net.daporkchop.lib.binary.io.OldDataIn;
import net.daporkchop.lib.binary.io.OldDataOut;
import net.daporkchop.lib.common.function.io.IOBiConsumer;
import net.daporkchop.lib.common.function.io.IOFunction;

import java.io.IOException;

/**
 * A serializer can read and write objects to and from their binary representation
 *
 * @author DaPorkchop_
 */
public interface Serializer<T> {
    /**
     * Convenience method to define a serializer from a reader and writer function
     *
     * @param writer a function that can write an object. see {@link #write(Object, OldDataOut)}
     * @param reader a function that can read an object. see {@link #read(OldDataIn)}
     * @param <T>    the type of object that can be serialized
     * @return a new serializer that can serialize objects using the given reader and writer functions
     */
    static <T> Serializer<T> of(@NonNull IOBiConsumer<T, OldDataOut> writer, @NonNull IOFunction<OldDataIn, T> reader) {
        return new Serializer<T>() {
            @Override
            public void write(@NonNull T value, @NonNull OldDataOut out) throws IOException {
                writer.acceptThrowing(value, out);
            }

            @Override
            public T read(@NonNull OldDataIn in) throws IOException {
                return reader.applyThrowing(in);
            }
        };
    }

    /**
     * Writes (encodes) a value
     *
     * @param value the value to encode
     * @param out   a {@link OldDataOut} to write data to
     * @throws IOException if an IO exception occurs you dummy
     */
    void write(@NonNull T value, @NonNull OldDataOut out) throws IOException;

    /**
     * Reads (decodes) a value
     *
     * @param in a {@link OldDataIn} to read data from
     * @return the decoded value. must not be null!
     * @throws IOException if an IO exception occurs you dummy
     */
    T read(@NonNull OldDataIn in) throws IOException;
}
