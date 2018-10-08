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

package net.daporkchop.lib.primitive.generator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayDeque;
import java.util.Collection;

@Accessors(chain = true)
@Setter
@Getter
@NoArgsConstructor
public class Primitive {
    public static final Collection<Primitive> primitives = new ArrayDeque<>();
    public static final String FULLNAME_DEF = "_P%d_";
    public static final String NAME_DEF = "_p%d_";
    public static final String HASHCODE_DEF = "_hashCodeP%d_";

    static {
        primitives.add(
                new Primitive()
                        .setFullName("Integer")
                        .setName("int")
                        .setHashCode("(x >> 24) ^ (x >> 16) ^ (x >> 8) ^ x")
        );
        primitives.add(
                new Primitive()
                .setFullName("Object")
                .setName("Object")
                .setHashCode("java.util.Objects.hashCode(x)")
        );
    }

    @NonNull
    private String fullName;
    @NonNull
    private String name;
    @NonNull
    private String hashCode;

    public static int countVariables(@NonNull String filename) {
        for (int i = 0; ; i++) {
            String s = String.format(FULLNAME_DEF, i);
            if (!filename.contains(s)) {
                return i;
            }
        }
    }

    public String format(@NonNull String text, int i) {
        return text
                .replaceAll(String.format(FULLNAME_DEF, i), this.fullName)
                .replaceAll(String.format(NAME_DEF, i), this.name)
                .replaceAll(String.format(HASHCODE_DEF, i), this.hashCode);
    }

    @Override
    public String toString() {
        return this.fullName;
    }
}
