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

package net.daporkchop.lib.minecraft.save;

import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Path;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * A save format for opening {@link Save}s.
 * <p>
 * All implementations are expected to be completely stateless.
 *
 * @author DaPorkchop_
 */
public interface SaveFormat {
    /**
     * Open the {@link Save} at the given path.
     *
     * @param root    a {@link Path} indicating the root directory
     * @param options the {@link SaveOptions} to be used for opening the given save
     * @return the opened {@link Save}
     * @throws IllegalArgumentException if the save could not be opened
     */
    default Save open(@NonNull Path root, @NonNull SaveOptions options) throws IOException {
        Save save = this.tryOpen(root, options);
        checkState(save != null, "Couldn't open save at \"%s\" (options: %s)", root, options);
        return save;
    }

    /**
     * Attempts to open the {@link Save} at the given path.
     *
     * @param root    a {@link Path} indicating the root directory
     * @param options the {@link SaveOptions} to be used for opening the given save
     * @return the opened {@link Save}, or {@code null} if it could not be opened
     */
    Save tryOpen(@NonNull Path root, @NonNull SaveOptions options) throws IOException;
}
