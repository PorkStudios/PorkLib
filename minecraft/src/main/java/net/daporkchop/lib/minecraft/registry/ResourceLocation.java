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

package net.daporkchop.lib.minecraft.registry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.common.cache.Cache;
import net.daporkchop.lib.common.cache.ThreadCache;
import net.daporkchop.lib.common.misc.string.PUnsafeStrings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Minecraft-style resource location (also known as a registry name).
 *
 * @author DaPorkchop_
 */
@Getter
public final class ResourceLocation {
    protected static final Pattern        VALIDATION_PATTERN = Pattern.compile("^([^:]+):([^:]+)$");
    protected static final Cache<Matcher> MATCHER_CACHE      = ThreadCache.soft(() -> VALIDATION_PATTERN.matcher(""));

    @NonNull
    private final String modid;

    @NonNull
    private final String name;

    @Getter(AccessLevel.NONE)
    private int hashCode = 0;

    public ResourceLocation(@NonNull String modid, @NonNull String name) {
        this(String.format("%s:%s", modid, name));
    }

    public ResourceLocation(@NonNull String name) {
        Matcher matcher = MATCHER_CACHE.get();
        matcher.reset(name);
        if (!matcher.find())    {
            throw new IllegalArgumentException(String.format("Invalid resource location: \"%s\"!", name));
        }
        this.modid = matcher.group(1);
        this.name = matcher.group(2);
    }

    @Override
    public int hashCode() {
        int hashCode = this.hashCode;
        if (hashCode == 0)  {
            //compute hash
            for (char c : PUnsafeStrings.unwrap(this.modid))  {
                hashCode = hashCode * 31 + c;
            }
            hashCode = hashCode * 31 + ':';
            for (char c : PUnsafeStrings.unwrap(this.name))  {
                hashCode = hashCode * 31 + c;
            }
            if (hashCode == 0)  {
                hashCode = 1;
            }
            this.hashCode = hashCode;
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)    {
            return true;
        } else if (obj instanceof ResourceLocation) {
            ResourceLocation other = (ResourceLocation) obj;
            return this.modid.equals(other.modid) && this.name.equals(other.name);
        } else if (obj instanceof String)   {
            //check if the toString value is identical
            String other = (String) obj;
            return other.length() == this.modid.length() + this.name.length() + 1
                    && other.startsWith(this.modid)
                    && other.endsWith(this.name)
                    && other.charAt(this.modid.length()) == ':';
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("%s:%s", this.modid, this.name);
    }
}
