/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.http.parameter;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.UTF8;

/**
 * @author DaPorkchop_
 */
public interface Parameter<T> {
    String getName();

    T getValue();

    default String getStringValue() {
        return this.getValue().toString();
    }

    default byte[] getValueEncoded()    {
        return this.getStringValue().getBytes(UTF8.utf8);
    }

    @RequiredArgsConstructor
    @Getter
    class Simple implements Parameter<String>   {
        @NonNull
        private final String name;

        @NonNull
        private final String value;

        @Override
        public int hashCode() {
            return this.name.hashCode() * 31 + this.value.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)    {
                return true;
            } else if (obj instanceof Parameter)    {
                Parameter other = (Parameter) obj;
                return this.name.equals(other.getName()) && this.value.equals(other.getValue());
            } else {
                return false;
            }
        }

        @Override
        protected Parameter<String> clone() throws CloneNotSupportedException {
            return new Simple(this.name, this.value);
        }

        @Override
        public String toString() {
            return String.format("%s -> %s", this.name, this.value);
        }
    }
}
