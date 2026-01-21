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

package net.daporkchop.lib.http.util.exception;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.StatusCode;

/**
 * An {@link HttpException} containing a user-defined {@link StatusCode} (to avoid having to define implementations of {@link HttpException} for every HTTP
 * status code).
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class GenericHttpException extends HttpException {
    @NonNull
    private final StatusCode status;

    public GenericHttpException(@NonNull StatusCode status, boolean fillInStackTrace) {
        super(null, null, true, fillInStackTrace);
        this.status = status;
    }

    public GenericHttpException(@NonNull StatusCode status, String s) {
        super(s);
        this.status = status;
    }

    public GenericHttpException(@NonNull StatusCode status, String s, Throwable throwable) {
        super(s, throwable);
        this.status = status;
    }

    public GenericHttpException(@NonNull StatusCode status, Throwable throwable) {
        super(throwable);
        this.status = status;
    }

    protected GenericHttpException(@NonNull StatusCode status, String s, Throwable throwable, boolean noSuppress, boolean fillInStackTrace) {
        super(s, throwable, noSuppress, fillInStackTrace);
        this.status = status;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        return message == null ? this.status.toString() : message;
    }
}
