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

package net.daporkchop.lib.primitive.generator;

import lombok.NonNull;

/**
 * Replaces code file contents.
 * <p>
 * Note that the index parameter is optional, and will be {@code -1} if not given.
 *
 * @author DaPorkchop_
 */
public interface Replacer {
    /**
     * Processes a file name.
     *
     * @param name  the current name
     * @param index the current primitive index
     * @param buffer
     * @return the processed name, or {@code null} if generation of the file should be stopped
     */
    String processName(@NonNull String name, int index, StringBuffer buffer);

    /**
     * Processes a file's contents.
     *
     * @param code  the current file contents
     * @param index the current primitive index
     * @param buffer
     * @return the processed contents, or {@code null} if generation of the file should be stopped
     */
    String processCode(@NonNull String code, int index, StringBuffer buffer);
}
