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

package net.daporkchop.lib.logging;

import lombok.NonNull;
import net.daporkchop.lib.common.util.Formatter;

/**
 * @author DaPorkchop_
 */
public interface Logging {
    Logger logger = Logger.DEFAULT_LOG;

    default String format(@NonNull String text, @NonNull Object... params) {
        return Formatter.format(text, params);
    }

    default String javaFormat(@NonNull String text, @NonNull Object... params) {
        return String.format(text, params);
    }

    default RuntimeException exception(@NonNull String text) {
        return new RuntimeException(text);
    }

    default RuntimeException exception(@NonNull String text, @NonNull Object... params) {
        return new RuntimeException(this.format(text, params));
    }

    default RuntimeException exception(@NonNull String text, @NonNull Throwable t, @NonNull Object... params) {
        return new RuntimeException(this.format(text, params), t);
    }

    default RuntimeException exception(@NonNull Throwable t) {
        return new RuntimeException(t);
    }

    default String stringify(@NonNull Throwable t) {
        return this.format("${0}: ${1}", t.getClass(), t.getMessage());
    }
}
