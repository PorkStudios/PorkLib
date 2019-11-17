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

package net.daporkchop.lib.http.request.auth;

import lombok.NonNull;
import net.daporkchop.lib.http.request.HeaderRewriter;

/**
 * Base interface for implementations of HTTP authentication methods.
 *
 * @author DaPorkchop_
 */
public interface Authentication extends HeaderRewriter {
    /**
     * Gets an {@link Authentication} instance for use in situations where no authentication should be used.
     *
     * @return a dummy, no-op {@link Authentication} instance
     */
    static Authentication none() {
        return headers -> {};
    }

    /**
     * Gets an {@link Authentication} instance suitable for use with HTTP basic authentication (aka "basic access authentication") with the given
     * credentials.
     * <p>
     * See https://en.wikipedia.org/wiki/Basic_access_authentication
     *
     * @param username the username
     * @param password the password
     * @return an {@link Authentication} instance suitable for HTTP authentication with the given credentials
     */
    static Authentication basic(@NonNull String username, @NonNull String password) {
        return new HTTPBasicAuth(username, password);
    }
}
