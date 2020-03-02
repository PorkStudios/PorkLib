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

package net.daporkchop.lib.common.util.exception;

import static net.daporkchop.lib.common.misc.string.PStrings.fastFormat;

/**
 * Thrown when a value is too large to fit within some smaller data type.
 *
 * @author DaPorkchop_
 */
public class ValueOutOfBoundsException extends IllegalArgumentException {
    public ValueOutOfBoundsException(String s) {
        super(s);
    }

    public ValueOutOfBoundsException(byte value, byte expectedMin, byte expectedMax)  {
        super(fastFormat("%s, expected %s <= value < %s", value, expectedMin, expectedMax));
    }

    public ValueOutOfBoundsException(short value, short expectedMin, short expectedMax)  {
        super(fastFormat("%s, expected %s <= value < %s", value, expectedMin, expectedMax));
    }

    public ValueOutOfBoundsException(char value, char expectedMin, char expectedMax)  {
        super(fastFormat("%s, expected %s <= value < %s", value, expectedMin, expectedMax));
    }

    public ValueOutOfBoundsException(int value, int expectedMin, int expectedMax)  {
        super(fastFormat("%s, expected %s <= value < %s", value, expectedMin, expectedMax));
    }

    public ValueOutOfBoundsException(long value, long expectedMin, long expectedMax)  {
        super(fastFormat("%s, expected %s <= value < %s", value, expectedMin, expectedMax));
    }

    public ValueOutOfBoundsException(float value, float expectedMin, float expectedMax)  {
        super(fastFormat("%s, expected %s <= value < %s", value, expectedMin, expectedMax));
    }

    public ValueOutOfBoundsException(double value, double expectedMin, double expectedMax)  {
        super(fastFormat("%s, expected %s <= value < %s", value, expectedMin, expectedMax));
    }
}
