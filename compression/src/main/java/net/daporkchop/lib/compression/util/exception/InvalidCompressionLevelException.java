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

package net.daporkchop.lib.compression.util.exception;

import lombok.NonNull;
import net.daporkchop.lib.compression.CompressionProvider;

/**
 * Thrown when an invalid compression level is given.
 *
 * @author DaPorkchop_
 */
public final class InvalidCompressionLevelException extends IllegalArgumentException {
    public static int validate(int level, @NonNull CompressionProvider provider) throws InvalidCompressionLevelException    {
        if (level < provider.levelFast() || level > provider.levelBest()) {
            throw new InvalidCompressionLevelException(level, provider);
        }

        return level;
    }

    public InvalidCompressionLevelException(int level, @NonNull CompressionProvider provider) {
        super(String.format("Invalid level %d (must be in range %d-%d)", level, provider.levelFast(), provider.levelBest()));
    }
}
