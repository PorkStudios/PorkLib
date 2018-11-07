package net.daporkchop.lib.binary.data;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;

/**
 * @author DaPorkchop_
 */
public interface Serializer<T> {
    void write(@NonNull T val, @NonNull DataOut out);

    T read(@NonNull DataIn in);
}
