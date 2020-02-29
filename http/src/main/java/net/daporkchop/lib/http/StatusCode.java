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

package net.daporkchop.lib.http;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.util.Constants;
import net.daporkchop.lib.http.util.StatusCodes;

/**
 * An abstract representation of an HTTP status code.
 *
 * @author DaPorkchop_
 * @see StatusCodes
 */
public interface StatusCode {
    /**
     * Gets an HTTP status code by its numeric ID.
     *
     * @param id the numeric ID of the HTTP status code
     * @return a non-null instance of {@link StatusCode} with the given numeric ID
     */
    static StatusCode of(int id) {
        StatusCode code = Constants.STATUS_CODES_BY_NUMERIC_ID.get(id);
        return code == null ? new UnknownNoName(id) : code;
    }

    /**
     * Gets an HTTP status code by its numeric ID.
     *
     * @param id  the numeric ID of the HTTP status code
     * @param msg the message of the HTTP status code
     * @return a non-null instance of {@link StatusCode} with the given numeric ID
     */
    static StatusCode of(int id, @NonNull CharSequence msg) {
        StatusCode code = Constants.STATUS_CODES_BY_NUMERIC_ID.get(id);
        return code == null ? new UnknownNamed(msg, id) : code;
    }

    /**
     * @return this status code's message (textual representation)
     */
    CharSequence name();

    /**
     * @return the status code's numeric ID
     */
    int code();

    /**
     * @return an additional textual error message that will be displayed on error pages, or {@code null} if none should be displayed
     */
    default CharSequence errorMessage() {
        return null;
    }

    /**
     * An unknown HTTP status code with no message (only the numeric ID).
     *
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true)
    final class UnknownNoName implements StatusCode {
        private final int code;

        @Override
        public CharSequence name() {
            return "UNKNOWN";
        }

        @Override
        public int hashCode() {
            return this.code;
        }

        @Override
        public boolean equals(Object o) {
            return o == this || (o instanceof StatusCode && ((StatusCode) o).code() == this.code);
        }

        @Override
        public String toString() {
            return String.format("StatusCode(%d UNKNOWN)", this.code);
        }
    }

    /**
     * An unknown HTTP status code with a name.
     *
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true)
    final class UnknownNamed implements StatusCode {
        @NonNull
        private final CharSequence name;
        private final int          code;

        @Override
        public int hashCode() {
            return this.code;
        }

        @Override
        public boolean equals(Object o) {
            return o == this || (o instanceof StatusCode && ((StatusCode) o).code() == this.code);
        }

        @Override
        public String toString() {
            return String.format("StatusCode(%d %s)", this.code, this.name);
        }
    }
}
