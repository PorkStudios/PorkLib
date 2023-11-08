/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package net.daporkchop.lib.natives.util.exception;

import net.daporkchop.lib.common.pool.recycler.Recycler;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.common.util.PorkUtil;

/**
 * Thrown when the current platform does not support native libraries.
 *
 * @author DaPorkchop_
 */
public final class NativeFeaturesUnavailableException extends RuntimeException {
    public NativeFeaturesUnavailableException() {
        super();
    }

    public NativeFeaturesUnavailableException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        Recycler<StringBuilder> recycler = PorkUtil.stringBuilderRecycler();
        StringBuilder builder = recycler.allocate();

        builder.append("Arch: ").append(PlatformInfo.ARCHITECTURE.name())
                .append(", OS: ").append(PlatformInfo.OPERATING_SYSTEM.name());
        String msg = super.getMessage();
        if (msg != null) {
            builder.append(", ").append(msg);
        }

        String result = builder.toString();
        recycler.release(builder); //return builder to the recycler
        return result;
    }
}
