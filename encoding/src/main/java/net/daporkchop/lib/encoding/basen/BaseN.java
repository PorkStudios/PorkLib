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

package net.daporkchop.lib.encoding.basen;

import lombok.NonNull;
import net.daporkchop.lib.encoding.util.FastCharIntMap;
import net.daporkchop.lib.math.primitive.DivMod;

import java.util.Arrays;

/**
 * Allows for defining a method to encode a byte[] to a string using a given alphabet
 *
 * @author DaPorkchop_
 */
public class BaseN {
    public static BaseN of(@NonNull String alphabet)    {
        return new BaseN(alphabet);
    }

    public final char[] alphabet;
    public final int length;
    public final FastCharIntMap indexes = new FastCharIntMap();
    public final char zero;

    protected BaseN(String alphabet) {
        if (alphabet == null || alphabet.isEmpty())
            throw new IllegalArgumentException("Alphabet cannot be null or empty!");
        this.alphabet = alphabet.toCharArray();
        this.length = alphabet.length();

        //check for duplicates
        for (int i = 0; i < this.length; i++) {
            char c = this.alphabet[i];
            for (int j = 0; j < this.length; j++) {
                if (j == i) continue;
                if (this.alphabet[j] == c)
                    throw new IllegalArgumentException("Found duplicate characters in alphabet at indexes " + i + " and " + j + '!');
            }
        }

        for (int i = 0; i < this.length; i++) {
            this.indexes.put(this.alphabet[i], i);
        }

        this.zero = this.alphabet[0];
    }

    public String encode(byte[] data) {
        if (data.length == 0) return "";
        // Count leading zeros.
        int zeros = 0;
        while (zeros < data.length && data[zeros] == 0) {
            ++zeros;
        }
        // Convert base-256 digits to base-n digits (plus conversion to ASCII characters)
        data = Arrays.copyOf(data, data.length); // since we modify it in-place
        char[] encoded = new char[data.length * 2]; // upper bound
        int outputStart = encoded.length;
        for (int inputStart = zeros; inputStart < data.length; ) {
            encoded[--outputStart] = this.alphabet[DivMod.divmod(data, inputStart, 256, this.length)];
            if (data[inputStart] == 0) {
                ++inputStart; // optimization - skip leading zeros
            }
        }
        // Preserve exactly as many leading encoded zeros in output as there were leading zeros in input.
        while (outputStart < encoded.length && encoded[outputStart] == this.zero) {
            ++outputStart;
        }
        while (--zeros >= 0) {
            encoded[--outputStart] = this.zero;
        }
        // Return encoded string (including encoded leading zeros).
        return new String(encoded, outputStart, encoded.length - outputStart);
    }

    public byte[] decode(String input) {
        if (input.length() == 0) {
            return new byte[0];
        }
        // Convert the basen-encoded ASCII chars to a basen byte sequence (basen digits).
        byte[] inputn = new byte[input.length()];
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            int digit = this.indexes.get(c);
            if (digit < 0) {
                throw new IllegalArgumentException("Illegal character " + c + " at position " + i);
            }
            inputn[i] = (byte) digit;
        }
        // Count leading zeros.
        int zeros = 0;
        while (zeros < inputn.length && inputn[zeros] == 0) {
            ++zeros;
        }
        // Convert base-n digits to base-256 digits.
        byte[] decoded = new byte[input.length()];
        int outputStart = decoded.length;
        for (int inputStart = zeros; inputStart < inputn.length; ) {
            decoded[--outputStart] = DivMod.divmod(inputn, inputStart, this.length, 256);
            if (inputn[inputStart] == 0) {
                ++inputStart; // optimization - skip leading zeros
            }
        }
        // Ignore extra leading zeroes that were added during the calculation.
        while (outputStart < decoded.length && decoded[outputStart] == 0) {
            ++outputStart;
        }
        // Return decoded data (including original number of leading zeros).
        return Arrays.copyOfRange(decoded, outputStart - zeros, decoded.length);
    }
}
