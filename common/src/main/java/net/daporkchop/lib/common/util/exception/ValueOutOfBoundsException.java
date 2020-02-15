/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.common.util.exception;

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
        super(String.format("%d is not within expected bounds (min: %d, max: %d)", value, expectedMin, expectedMax));
    }

    public ValueOutOfBoundsException(short value, short expectedMin, short expectedMax)  {
        super(String.format("%d is not within expected bounds (min: %d, max: %d)", value, expectedMin, expectedMax));
    }

    public ValueOutOfBoundsException(char value, char expectedMin, char expectedMax)  {
        super(String.format("%d is not within expected bounds (min: %d, max: %d)", value, expectedMin, expectedMax));
    }

    public ValueOutOfBoundsException(int value, int expectedMin, int expectedMax)  {
        super(String.format("%d is not within expected bounds (min: %d, max: %d)", value, expectedMin, expectedMax));
    }

    public ValueOutOfBoundsException(long value, long expectedMin, long expectedMax)  {
        super(String.format("%d is not within expected bounds (min: %d, max: %d)", value, expectedMin, expectedMax));
    }

    public ValueOutOfBoundsException(float value, float expectedMin, float expectedMax)  {
        super(String.format("%f is not within expected bounds (min: %f, max: %f)", value, expectedMin, expectedMax));
    }

    public ValueOutOfBoundsException(double value, double expectedMin, double expectedMax)  {
        super(String.format("%f is not within expected bounds (min: %f, max: %f)", value, expectedMin, expectedMax));
    }
}
