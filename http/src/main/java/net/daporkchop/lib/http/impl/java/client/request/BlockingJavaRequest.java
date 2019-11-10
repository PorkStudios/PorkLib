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

package net.daporkchop.lib.http.impl.java.client.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.function.PFunctions;
import net.daporkchop.lib.http.StatusCode;
import net.daporkchop.lib.http.client.request.BlockingRequest;
import net.daporkchop.lib.http.impl.java.client.JavaHttpClient;
import net.daporkchop.lib.http.util.header.Header;
import net.daporkchop.lib.http.util.header.HeaderMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of {@link BlockingRequest} for {@link JavaHttpClient}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class BlockingJavaRequest implements BlockingRequest, HeaderMap {
    protected final JavaHttpClient    client;
    @Getter(AccessLevel.NONE)
    protected final HttpURLConnection connection;

    protected final StatusCode statusCode;

    protected final List<Header>        headerList;
    protected final Map<String, Header> headerMap;

    public BlockingJavaRequest(@NonNull JavaHttpClient client, @NonNull HttpURLConnection connection) throws IOException {
        this.client = client;
        this.connection = connection;

        connection.connect();

        this.statusCode = StatusCode.of(connection.getResponseCode(), connection.getResponseMessage());

        Map<String, List<String>> headers = connection.getHeaderFields();
        this.headerList = new ArrayList<>(headers.size());
        headers.forEach((key, value) -> this.headerList.add(value.size() == 1 ? new Header.Default(key, value.get(0)) : new Header.DefaultList(key, value)));
        this.headerMap = this.headerList.stream().collect(Collectors.toMap(Header::key, PFunctions.identity()));
    }

    @Override
    public HeaderMap headers() {
        return this;
    }

    @Override
    public OutputStream output() throws IOException {
        return this.connection.getOutputStream();
    }

    @Override
    public InputStream input() throws IOException {
        return this.connection.getInputStream();
    }

    @Override
    public void close() throws IOException {
        this.connection.disconnect();
    }

    //
    //
    // HeaderMap implementations
    //
    //

    @Override
    public int count() {
        return this.headerList.size();
    }

    @Override
    public Header get(int index) {
        return this.headerList.get(index);
    }

    @Override
    public Header get(@NonNull String key) {
        return this.headerMap.get(key);
    }
}
