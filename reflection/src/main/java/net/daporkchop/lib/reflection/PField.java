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

package net.daporkchop.lib.reflection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.lang.reflect.Field;

import static net.daporkchop.lib.common.util.PConstants.p_exception;
import static net.daporkchop.lib.common.util.PUnsafe.objectFieldOffset;

/**
 * A Java field
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PField<V> {
    protected final long offset;
    @NonNull
    protected final Type type;
    protected final int modifiers;

    public static <V> PField<V> of(@NonNull Class<?> clazz, @NonNull String name)   {
        try {
            return of(clazz.getDeclaredField(name));
        } catch (NoSuchFieldException e)    {
            throw p_exception(e);
        }
    }

    public static <V> PField<V> of(@NonNull Field field) {
        return new PField<>(objectFieldOffset(field), Type.getType(field), field.getModifiers());
    }
}
