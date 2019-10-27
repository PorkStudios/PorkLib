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

package net.daporkchop.lib.http.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.StatusCode;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.charset.StandardCharsets;

/**
 * The official HTTP status codes.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public enum StatusCodes implements StatusCode {
    // 1xx
    Continue(100),
    Switching_Protocols(101),
    Processing(102),
    Early_Hints(103),
    // 2xx
    OK(200),
    Created(201),
    Accepted(202),
    Non_Authoritative_Information(203, "Non-Authoritative Information"),
    No_Content(204),
    Reset_Content(205),
    Partial_Content(206),
    Multi_Status(207, "Multi-Status"),
    Already_Reported(208),
    IM_Used(209),
    // 3xx
    Multiple_Choices(300),
    Moved_Permanently(301),
    Moved_Temporarily(302),
    See_Other(303),
    Not_Modified(304),
    Temporary_Redirect(307),
    Permanent_Redirect(308),
    // 4xx
    Bad_Request(400),
    Unauthorized(401),
    Payment_Required(402),
    Forbidden(403),
    Not_Found(404, "The requested URL was not found on this server."),
    Method_Not_Allowed(405),
    Not_Acceptable(406),
    Proxy_Authentication_Required(407),
    Request_Timeout(408),
    Conflict(409),
    Gone(410),
    Length_Required(411),
    Precondition_Failed(412),
    Payload_Too_Large(413),
    URI_Too_Long(414),
    Unsupported_Media_Type(415),
    Range_Not_Satisfiable(416),
    Expectation_Failed(417),
    Im_A_Teapot(418, "I'm a teapot", "See <a href=\"https://tools.ietf.org/html/rfc2324\">https://tools.ietf.org/html/rfc2324</a>, section 2.3.2."),
    Misdirected_Request(421),
    Unprocessable_Entity(422),
    Locked(423),
    Failed_Dependency(424),
    Too_Early(425),
    Upgrade_Required(426),
    Precondition_Required(428),
    Too_Many_Requests(429),
    Request_Header_Fields_Too_Large(431),
    Unavailable_For_Legal_Reasons(451),
    // 5xx
    Internal_Server_Error(500),
    Not_Implemented(501),
    Bad_Gateway(502),
    Service_Unavailable(503),
    Gateway_Timeout(504),
    HTTP_Version_Not_Supported(505),
    Variant_Also_Negotiates(506),
    Insufficient_Storage(507),
    Loop_Detected(508),
    Not_Extended(510),
    Network_Authentication_Required(511)
    ;

    @Getter
    private final String msg;
    @Getter
    private final String errorMessage;
    private final byte[] encodedValue;
    @Getter
    private final int code;

    StatusCodes(int code)    {
        this(code, null, null);
    }

    StatusCodes(int code, String errorMessage)    {
        this(code, null, errorMessage);
    }

    StatusCodes(int code, String name, String errorMessage)    {
        this.code = code;
        this.errorMessage = errorMessage;
        this.msg = name = (name == null ? this.name().replace('_', ' ') : name);
        this.encodedValue = String.format(" %d %s", code, name).getBytes(StandardCharsets.US_ASCII);
    }

    @Override
    public ByteBuf encodedValue() {
        return Unpooled.wrappedBuffer(this.encodedValue);
    }

    @Override
    public String toString() {
        return String.format("StatusCode(%d %s)", this.code, this.name());
    }
}
