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

package net.daporkchop.lib.encoding.basen;

import lombok.NonNull;
import net.daporkchop.lib.common.misc.string.PUnsafeStrings;
import net.daporkchop.lib.encoding.util.FastCharIntMap;
import net.daporkchop.lib.math.primitive.PMath;

import java.util.Arrays;

/**
 * Allows for defining a method to encode a {@code byte[]} to a {@link String} using a given alphabet.
 *
 * @author DaPorkchop_
 */
public final class BaseN {
    public static BaseN of(@NonNull String alphabet) {
        return new BaseN(alphabet);
    }

    protected final char[] alphabet;
    protected final FastCharIntMap indexes;
    protected final int length;
    protected final char zero;

    private BaseN(@NonNull String alphabet) {
        if (alphabet.isEmpty()){
            throw new IllegalArgumentException("Alphabet cannot be null or empty!");
        }
        this.alphabet = PUnsafeStrings.unwrap(alphabet);
        this.length = alphabet.length();

        //check for duplicates
        for (int i = 0; i < this.length; i++) {
            char c = this.alphabet[i];
            for (int j = 0; j < this.length; j++) {
                if (j == i) continue;
                if (this.alphabet[j] == c){
                    throw new IllegalArgumentException(String.format("Found duplicate character '%c' in alphabet at indexes %d and %d!", this.alphabet[i], i, j));
                }
            }
        }

        this.indexes = new FastCharIntMap();
        for (int i = 0; i < this.length; i++) {
            this.indexes.put(this.alphabet[i], i);
        }

        this.zero = this.alphabet[0];
    }

    public String encode(@NonNull byte[] data) {
        if (data.length == 0)   {
            return "";
        }
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
            encoded[--outputStart] = this.alphabet[PMath.divmod(data, inputStart, 256, this.length)];
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

    public byte[] decode(@NonNull CharSequence input) {
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
            decoded[--outputStart] = PMath.divmod(inputn, inputStart, this.length, 256);
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
