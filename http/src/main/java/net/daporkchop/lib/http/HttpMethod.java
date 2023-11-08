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
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.function.PFunctions;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The different HTTP request types.
 *
 * @author DaPorkchop_
 * @see <a href="https://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol#Request_methods">en.wikipedia.org/wiki/Hypertext_Transfer_Protocol#Request_methods</a>
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum HttpMethod {
    /**
     * The GET method requests a representation of the specified resource. Requests using GET should only retrieve data and should have no other effect.
     */
    GET(false, true),
    /**
     * The HEAD method asks for a response identical to that of a GET request, but without the response body. This is useful for retrieving
     * meta-information written in response headers, without having to transport the entire content.
     */
    HEAD(false, false),
    /**
     * The POST method requests that the server accept the entity enclosed in the request as a new subordinate of the web resource identified by the
     * URI. The data POSTed might be, for example, an annotation for existing resources; a message for a bulletin board, newsgroup, mailing list, or
     * comment thread; a block of data that is the result of submitting a web form to a keys-handling process; or an item to add to a database.
     */
    POST(true, true),
    //PUT,
    //DELETE,
    /**
     * The CONNECT method converts the request connection to a transparent TCP/IP tunnel, usually to facilitate SSL-encrypted communication (HTTPS)
     * through an unencrypted HTTP proxy.
     */
    CONNECT(true, true)
    //OPTIONS,
    //TRACE,
    //PATCH
    ;

    public static final Map<String, HttpMethod> LOOKUP = Collections.unmodifiableMap(Arrays.stream(values()).collect(Collectors.toMap(HttpMethod::name, PFunctions.identity())));

    private final byte[] asciiName = this.name().getBytes(StandardCharsets.US_ASCII);
    private final boolean hasRequestBody;
    private final boolean hasResponseBody;
}
