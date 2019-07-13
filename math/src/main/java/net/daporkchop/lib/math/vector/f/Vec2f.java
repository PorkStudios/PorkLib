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

package net.daporkchop.lib.math.vector.f;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A 2-dimensional vector.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
public final class Vec2f implements FloatVector2 {
    protected final float x;
    protected final float y;

    @Override
    public FloatVector2 add(float x, float y) {
        return new Vec2f(this.x + x, this.y + y);
    }

    @Override
    public FloatVector2 subtract(float x, float y) {
        return new Vec2f(this.x - x, this.y - y);
    }

    @Override
    public FloatVector2 multiply(float x, float y) {
        return new Vec2f(this.x * x, this.y * y);
    }

    @Override
    public FloatVector2 divide(float x, float y) {
        return new Vec2f(this.x / x, this.y / y);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FloatVector2)) {
            return false;
        }

        FloatVector2 vec = (FloatVector2) obj;
        return this.x == vec.getX() && this.y == vec.getY();
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.x) * 926373397 + Float.floatToIntBits(this.y);
    }

    @Override
    public String toString() {
        return String.format("Vec2f(%f,%f)", this.x, this.y);
    }
}
