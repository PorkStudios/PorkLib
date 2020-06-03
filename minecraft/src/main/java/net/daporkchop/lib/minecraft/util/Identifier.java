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

package net.daporkchop.lib.minecraft.util;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.common.math.PMath;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ThreadRef;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * A namespaced identifier used by Minecraft to identify basically everything.
 * <p>
 * Based mostly on https://github.com/NukkitX/Nukkit/blob/f695a7ac2c5afe591cbd745ffe5b1236c110e208/src/main/java/cn/nukkit/utils/Identifier.java (which I
 * did a lot of).
 *
 * @author DaPorkchop_
 */
@Getter
public final class Identifier implements Comparable<Identifier> {
    public static final Identifier EMPTY = new Identifier("", "", ":");

    protected static final Pattern VALIDATION_PATTERN = Pattern.compile("^(?>minecraft:|([a-zA-Z0-9_]*):)?([a-zA-Z0-9_]*)$");
    protected static final Ref<Matcher> MATCHER_CACHE = ThreadRef.regex(VALIDATION_PATTERN);

    private static final Lock READ_LOCK;
    private static final Lock WRITE_LOCK;

    //TODO: this needs to be able to be garbage-collected. in other words: i need a TreeMap with soft values
    private static final Map<String, Identifier> VALUES = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    static {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        READ_LOCK = lock.readLock();
        WRITE_LOCK = lock.writeLock();
    }

    private final String modid;
    private final String name;
    private final String fullName;

    private final transient int hashCode;

    private Identifier(String modid, String name, String fullName) {
        this.modid = modid;
        this.name = name;
        this.fullName = fullName;

        //dig my epic hash distribution
        this.hashCode = PMath.mix32(fullName.chars().asLongStream().reduce(0L, (a, b) -> PMath.mix64(a + b)));
    }

    public static Identifier fromString(@NonNull String identifier) {
        if (identifier.isEmpty()) {
            //check for empty before using matcher
            return EMPTY;
        }

        Matcher matcher = MATCHER_CACHE.get().reset(identifier);
        checkArg(matcher.find(), "Invalid identifier: \"%s\"", identifier);

        Identifier id;
        READ_LOCK.lock();
        try {
            id = VALUES.get(identifier);
        } finally {
            READ_LOCK.unlock();
        }

        if (id == null) {
            String namespace = matcher.group(1);
            String name = matcher.group(2).intern();
            String fullName = (namespace == null && !identifier.startsWith("minecraft:") ? "minecraft:" + name : identifier).intern();
            boolean defaultNamespace = namespace == null;
            namespace = defaultNamespace ? "minecraft" : namespace.intern();

            //create new identifier instance
            WRITE_LOCK.lock();
            try {
                //try get again in case identifier was created while obtaining write lock
                if ((id = VALUES.get(identifier)) == null) {
                    id = new Identifier(namespace, name, fullName);
                    if (defaultNamespace) {
                        //also put it into the map without minecraft: in the key to facilitate faster lookups when the prefix is omitted
                        VALUES.put(name, id);
                    }
                    VALUES.put(fullName, id);
                }
            } finally {
                WRITE_LOCK.unlock();
            }
        }
        return id;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public String toString() {
        return this.fullName;
    }

    @Override
    public int compareTo(Identifier o) {
        return this == o ? 0 : this.fullName.compareTo(o.fullName);
    }
}
