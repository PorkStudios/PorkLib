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

package net.daporkchop.lib.collection.generator;

/**
 * @author DaPorkchop_
 */
public enum Primitive {
    Byte("(byte) 0",
            "return in & 0xFF;",
            0),
    Short("(short) 0",
            "return in & 0xFFFF;",
            1),
    Character("(char) 0",
            "char",
            "return in & 0xFFFF;",
            1),
    Integer("0",
            "int",
            "return in & 0x7fffffff;",
            3),
    Long("0L",
            "long",
            "return ((int) ((in >> 32) & 0x7fffffff)) ^ ((int) (in & 0x7fffffff)) & 0x7fffffff;",
            "return in;",
            false,
            7),
    Float("0F",
            "float",
            "return Float.floatToIntBits(in);",
            3),
    Double("0D",
            "double",
            "long l = Double.doubleToLongBits(in);\n" +
                    "        return (int) ((((l >> 32) & 0x7fffffff)) ^ ((l & 0x7fffffff)) & 0x7fffffff);",
            "return Double.doubleToLongBits(in);",
            false,
            7),
    Boolean("false",
            "return in ? 1 : 0;",
            0),
    Object("null",
            "Object",
            "return java.util.Objects.hashCode(in);",
            "return (long) java.util.Objects.hashCode(in);",
            true,
            3);

    public final String displayName;
    public final String emptyValue;
    public final String hashFunction;
    public final String longHashFunction;
    public final String bytes;
    public final boolean generic;

    Primitive(String emptyValue,
              String hashFunction,
              int bytes) {
        this.hashFunction = hashFunction;
        this.longHashFunction = hashFunction;
        this.emptyValue = emptyValue;
        this.displayName = name().toLowerCase();
        this.bytes = String.valueOf(bytes);
        this.generic = false;
    }

    Primitive(String emptyValue,
              String displayName,
              String hashFunction,
              int bytes) {
        this(emptyValue, displayName, hashFunction, hashFunction, false, bytes);
    }

    Primitive(String emptyValue,
              String displayName,
              String hashFunction,
              String longHashFunction,
              boolean generic,
              int bytes) {
        this.hashFunction = hashFunction;
        this.longHashFunction = longHashFunction;
        this.emptyValue = emptyValue;
        this.displayName = displayName;
        this.bytes = String.valueOf(bytes);
        this.generic = generic;
    }
}
