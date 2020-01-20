/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.natives.zlib;

/**
 * The different modes supported by Zlib.
 *
 * @author DaPorkchop_
 */
public enum ZlibMode {
    /**
     * Zlib wrapping mode, uses a 6-byte header with an Adler32 checksum.
     * <p>
     * This is the default mode.
     */
    ZLIB,
    /**
     * Gzip wrapping mode, uses an (at least) 18-byte header with a CRC32 checksum.
     */
    GZIP,
    /**
     * Only for use by {@link PInflater}, automatically detects whether the input data is in the Zlib or Gzip format.
     * <p>
     * Note that this doesn't work for data encoded in the {@link #ZLIB_MODE_RAW} format.
     */
    AUTO,
    /**
     * "Raw" Deflate mode, no wrapping is applied to the compressed data.
     */
    RAW;
}
