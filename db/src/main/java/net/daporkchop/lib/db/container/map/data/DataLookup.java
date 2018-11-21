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

package net.daporkchop.lib.db.container.map.data;

import lombok.NonNull;
import net.daporkchop.lib.binary.Persistent;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.function.IOEConsumer;
import net.daporkchop.lib.db.container.map.DBMap;

import java.io.File;
import java.io.IOException;

/**
 * Allows reading an undefined amount of data from disk, using an identifier
 * stored as a long
 *
 * @author DaPorkchop_
 */
public interface DataLookup extends Persistent {
    default void init(@NonNull DBMap<?, ?> map, @NonNull File file) throws IOException {
        this.load();
    }

    /**
     * Returns a {@link DataIn} which will allow reading the data for the given entry
     *
     * @param id the id
     * @return an instance of {@link DataIn}, or null if not found
     * @throws IOException if a IO exception occurs you dummy
     */
    DataIn read(long id) throws IOException;

    /**
     * Write some data to the disk
     *
     * @param id     the previous id for this data, the data at this id will be overwritten. if
     *               this is a new bit of data, this should be -1
     * @param writer this function will be called once an output stream is successfully created. it
     *               will receive exactly one parameter, which will be an instance of {@link DataOut}
     *               that will write any data given to it to disk
     * @return the new id of the data. may be the same
     * @throws IOException if a IO exception occurs you dummy
     */
    long write(long id, @NonNull IOEConsumer<DataOut> writer) throws IOException;

    /**
     * Removes the data at a given id from disk. This behaviour is undefined, it may actually remove the
     * data, or it may just mark the disk region occupied by this data as free
     *
     * @param id the id of the data
     * @throws IOException if a IO exception occurs you dummy
     */
    void remove(long id) throws IOException;

    /**
     * Completely resets this {@link DataLookup}, removing all data. Implementations are expected to free
     * up disk resources occupied by the newly removed data.
     *
     * @throws IOException if a IO exception occurs you dummy
     */
    void clear() throws IOException;
}
