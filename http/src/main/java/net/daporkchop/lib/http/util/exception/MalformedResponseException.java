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

package net.daporkchop.lib.http.util.exception;

import net.daporkchop.lib.http.StatusCode;

/**
 * Thrown by the client when the server sends a malformed response.
 *
 * @author DaPorkchop_
 */
public class MalformedResponseException extends HTTPException {
    public MalformedResponseException() {
        super();
    }

    public MalformedResponseException(String s) {
        super(s);
    }

    public MalformedResponseException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public MalformedResponseException(Throwable throwable) {
        super(throwable);
    }

    protected MalformedResponseException(String s, Throwable throwable, boolean noSuppress, boolean fillInStackTrace) {
        super(s, throwable, noSuppress, fillInStackTrace);
    }

    @Override
    public StatusCode status() {
        return null;
    }
}
