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

package net.daporkchop.lib.http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;

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

    private final byte[] asciiName = this.name().getBytes(StandardCharsets.US_ASCII);
    private final boolean hasRequestBody;
    private final boolean hasResponseBody;
}
