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

package net.daporkchop.lib.minecraft.util;

import net.daporkchop.lib.minecraft.save.Save;

/**
 * The different write access levels that can be used for opening a {@link Save}.
 *
 * @author DaPorkchop_
 */
//does this really need to be an enum? i doubt there'll ever be need for other modes
public enum WriteAccess {
    /**
     * Opens the save in read-write mode. If either read or write access could not be acquired, an exception will be thrown.
     */
    WRITE_REQUIRED,
    /**
     * Makes a best-effort attempt to open the save in read-write mode, transparent falling back to read-only mode if write access could not be acquired.
     * <p>
     * If read access could not be acquired, an exception will be thrown.
     */
    WRITE_OPTIONAL,
    /**
     * Opens the save in read-only mode. If read access could not be acquired, an exception will be thrown.
     */
    READ_ONLY;
}
