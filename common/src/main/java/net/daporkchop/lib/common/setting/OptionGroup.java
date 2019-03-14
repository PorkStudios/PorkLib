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

package net.daporkchop.lib.common.setting;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
@Builder
public class OptionGroup {
    public static OptionGroup of(@NonNull Option option) {
        return new OptionGroup(Collections.singleton(option));
    }

    public static OptionGroup of(@NonNull Option... options) {
        return new OptionGroup(Arrays.stream(options).map(Objects::requireNonNull).collect(Collectors.toSet()));
    }

    public static OptionGroup of(@NonNull Object... objects) {
        Set<Option> options = new HashSet<>();
        for (Object o : objects) {
            if (o == null) {
                throw new NullPointerException();
            }
            if (o instanceof Option) {
                options.add((Option) o);
            } else if (o instanceof OptionGroup)    {
                options.addAll(((OptionGroup) o).options);
            } else if (o instanceof Collection) {
                ((Collection) o).forEach(o1 -> {
                    if (o instanceof Option) {
                        options.add((Option) o);
                    } else {
                        throw new IllegalArgumentException(String.format("Invalid class: %s", o.getClass().getCanonicalName()));
                    }
                });
            } else {
                throw new IllegalArgumentException(String.format("Invalid class: %s", o.getClass().getCanonicalName()));
            }
        }
        return new OptionGroup(options);
    }

    @NonNull
    @Singular
    private final Set<Option> options;

    public boolean isCompatible(@NonNull OptionGroup other) {
        return this.isCompatible(other.options);
    }

    public boolean isCompatible(@NonNull Set<Option> other) {
        return this.options.stream().noneMatch(other::contains);
    }

    public void addTo(@NonNull Map<Option, Object> map) {
        this.options.forEach(option -> map.put(option, option.getDefaultValue()));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof OptionGroup) {
            OptionGroup other = (OptionGroup) obj;
            return other.options.equals(this.options);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int i = 0;
        for (Option option : this.options) {
            i *= 1510816291 * option.hashCode();
        }
        return i;
    }
}
