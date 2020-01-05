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

package net.daporkchop.lib.http.util.exception;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.StatusCode;
import net.daporkchop.lib.http.util.StatusCodes;

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
    public static final HttpException Bad_Request                     = new GenericHttpException(StatusCodes.Bad_Request, false);
    public static final HttpException Method_Not_Allowed              = new GenericHttpException(StatusCodes.Method_Not_Allowed, false);
    public static final HttpException Payload_Too_Large               = new GenericHttpException(StatusCodes.Payload_Too_Large, false);
    public static final HttpException URI_Too_Long                    = new GenericHttpException(StatusCodes.URI_Too_Long, false);
    public static final HttpException Request_Header_Fields_Too_Large = new GenericHttpException(StatusCodes.Request_Header_Fields_Too_Large, false);
    public static final HttpException Internal_Server_Error           = new GenericHttpException(StatusCodes.Internal_Server_Error, false);

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
        return message == null ? String.format("%d %s", this.status.code(), this.status.msg()) : message;
    }
}
