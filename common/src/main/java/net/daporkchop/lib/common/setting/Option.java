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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * A single setting key
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class Option<T> {
    public static Object UNSET_VALUE = new Object();

    public static <T> OptionBuilder<T> builder() {
        return new OptionBuilder<>();
    }

    public static <T> Option<T> of(@NonNull String name, @NonNull T defaultValue) {
        return new OptionBuilder<T>().setName(name).setDefaultValue(defaultValue).build();
    }

    @NonNull
    private final String name;
    @NonNull
    private final T defaultValue;
    private boolean required = false;

    @Getter(AccessLevel.PRIVATE)
    private final int hash;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Option) {
            Option other = (Option) obj;
            return this.name.equals(other.name);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class OptionBuilder<T> {
        @NonNull
        private String name;

        @NonNull
        @SuppressWarnings("unchecked")
        private T defaultValue = (T) UNSET_VALUE;

        private boolean required = false;

        public Option<T> build() {
            if (this.name == null) {
                throw new IllegalStateException("name not set!");
            } else if (this.defaultValue == null) {
                throw new IllegalStateException("defaultValue not set!");
            } else {
                return new Option<>(this.name, this.defaultValue, this.required, this.name.hashCode());
            }
        }
    }
}
