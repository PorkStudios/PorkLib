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

package net.daporkchop.lib.http.util.exception;

import net.daporkchop.lib.http.StatusCode;

/**
 * An exception generated in the HTTP codec pipeline.
 *
 * @author DaPorkchop_
 */
public abstract class HttpException extends Exception {
    public HttpException() {
        super();
    }

    public HttpException(String s) {
        super(s);
    }

    public HttpException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public HttpException(Throwable throwable) {
        super(throwable);
    }

    protected HttpException(String s, Throwable throwable, boolean noSuppress, boolean fillInStackTrace) {
        super(s, throwable, noSuppress, fillInStackTrace);
    }

    /**
     * Gets a (possibly {@code null}) {@link StatusCode} associated with this exception.
     *
     * This may be used by other pipeline members to help describe the issue better.
     * @return a (possibly {@code null}) {@link StatusCode} associated with this exception
     */
    public abstract StatusCode status();
}
