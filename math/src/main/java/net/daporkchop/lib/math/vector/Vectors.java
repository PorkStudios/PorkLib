/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2021 DaPorkchop_
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

package net.daporkchop.lib.math.vector;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;

/**
 * Container class with static implementations of vector types.
 *
 * @author DaPorkchop_
 */
@UtilityClass
@Accessors(fluent = true)
class Vectors {
    static final int HASH0 = 128675773;
    static final int HASH1 = 659829659;
    static final int HASH2 = 1504861999;

    /**
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    @Getter
    static final class Vec2fImpl implements Vec2f {
        private final float x;
        private final float y;

        @Override
        public boolean equals(Object obj) {
            return Vec2f.equals(this, obj);
        }

        @Override
        public int hashCode() {
            return Vec2f.hashCode(this);
        }

        @Override
        public String toString() {
            return Vec2f.toString(this);
        }
    }

    /**
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    @Getter
    static final class Vec2dImpl implements Vec2d {
        private final double x;
        private final double y;

        @Override
        public boolean equals(Object obj) {
            return Vec2d.equals(this, obj);
        }

        @Override
        public int hashCode() {
            return Vec2d.hashCode(this);
        }

        @Override
        public String toString() {
            return Vec2d.toString(this);
        }
    }

    /**
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    @Getter
    static final class Vec2iImpl implements Vec2i {
        private final int x;
        private final int y;

        @Override
        public boolean equals(Object obj) {
            return Vec2i.equals(this, obj);
        }

        @Override
        public int hashCode() {
            return Vec2i.hashCode(this);
        }

        @Override
        public String toString() {
            return Vec2i.toString(this);
        }
    }

    /**
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    @Getter
    static final class Vec3dImpl implements Vec3d {
        private final double x;
        private final double y;
        private final double z;

        @Override
        public boolean equals(Object obj) {
            return Vec3d.equals(this, obj);
        }

        @Override
        public int hashCode() {
            return Vec3d.hashCode(this);
        }

        @Override
        public String toString() {
            return Vec3d.toString(this);
        }
    }

    /**
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    @Getter
    static final class Vec3fImpl implements Vec3f {
        private final float x;
        private final float y;
        private final float z;

        @Override
        public boolean equals(Object obj) {
            return Vec3f.equals(this, obj);
        }

        @Override
        public int hashCode() {
            return Vec3f.hashCode(this);
        }

        @Override
        public String toString() {
            return Vec3f.toString(this);
        }
    }

    /**
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    @Getter
    static final class Vec3iImpl implements Vec3i {
        private final int x;
        private final int y;
        private final int z;

        @Override
        public boolean equals(Object obj) {
            return Vec3i.equals(this, obj);
        }

        @Override
        public int hashCode() {
            return Vec3i.hashCode(this);
        }

        @Override
        public String toString() {
            return Vec3i.toString(this);
        }
    }
}
