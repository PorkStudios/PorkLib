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

package net.daporkchop.lib.http.header.map;

import lombok.NonNull;
import net.daporkchop.lib.http.header.Header;
import net.daporkchop.lib.http.header.HeaderImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A simple implementation of {@link MutableHeaderMap}.
 *
 * @author DaPorkchop_
 */
public class MutableHeaderMapImpl implements MutableHeaderMap {
    protected final List<Header>        list;
    protected final Map<String, Header> map;

    public MutableHeaderMapImpl() {
        this.list = new ArrayList<>();
        this.map = new HashMap<>();
    }

    public MutableHeaderMapImpl(@NonNull HeaderMap source) {
        this();
        this.putAll(source);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Header get(int index) throws IndexOutOfBoundsException {
        return this.list.get(index);
    }

    @Override
    public Header get(@NonNull String key) {
        return this.map.get(key.toLowerCase());
    }

    @Override
    public String put(@NonNull String key, @NonNull String value) {
        Header header = new HeaderImpl(key, value);
        key = key.toLowerCase();
        Header old = this.map.putIfAbsent(key, header);
        if (old == null) {
            //the header is new
            this.list.add(header);
            return null;
        } else {
            //the header already exists
            this.map.replace(key, old, header);
            this.list.set(this.list.indexOf(old), header);
            return old.value();
        }
    }

    @Override
    public String remove(@NonNull String key) {
        Header old = this.map.remove(key.toLowerCase());
        return old != null && this.list.remove(old) ? old.value() : null;
    }

    @Override
    public Header remove(int index) throws IndexOutOfBoundsException {
        Header header = this.list.remove(index);
        if (!this.map.remove(header.key().toLowerCase(), header))   {
            throw new IllegalStateException(String.format("Couldn't remove header at index %d (key \"%s\" (internal: \"%s\") is not present in map!)", index, header.key(), header.key().toLowerCase()));
        }
        return header;
    }

    @Override
    public void forEach(@NonNull Consumer<Header> callback) {
        this.list.forEach(callback);
    }

    @Override
    public void forEach(@NonNull BiConsumer<String, String> callback) {
        this.list.forEach(header -> callback.accept(header.key(), header.value()));
    }
}
