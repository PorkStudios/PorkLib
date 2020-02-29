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

package net.daporkchop.lib.hash;

import lombok.NonNull;

/**
 * Lightweight hash algorithms for use in non-cryptographic applications
 *
 * @author DaPorkchop_
 */
public interface PHash {
    static int hash(@NonNull String s)  {
        if (s.isEmpty()) {
            return 0;
        } else {
            int h = 0;
            for (int i = 0; i < s.length(); i++) {
                h = 31 * h + s.charAt(i);
            }
            return h;
        }
    }

    static long longHash(@NonNull String s)  {
        if (s.isEmpty()) {
            return 0L;
        } else {
            long h = 0L;
            for (int i = 0; i < s.length(); i++) {
                h = 31L * h + s.charAt(i);
            }
            return h;
        }
    }

    static int hash(@NonNull CharSequence s)    {
        if (s.length() == 0)    {
            return 0;
        } else {
            int h = 316105151;
            for (int i = s.length() - 1; i >= 0; i--)   {
                h = h * 978782141 + s.charAt(i) * 1215893573;
            }
            return h;
        }
    }

    static long longHash(@NonNull CharSequence s)    {
        if (s.length() == 0)    {
            return 0L;
        } else {
            long h = 8486681283805525963L;
            for (int i = s.length() - 1; i >= 0; i--)   {
                h = h * 2227328094050660801L + s.charAt(i) * 5801442270845775361L;
            }
            return h;
        }
    }

    static int hash(@NonNull byte[] s)    {
        if (s.length == 0)    {
            return 0;
        } else {
            int h = 316105151;
            for (int i = s.length - 1; i >= 0; i--)   {
                h = h * 978782141 + (s[i] & 0xFF) * 1215893573;
            }
            return h;
        }
    }

    static long longHash(@NonNull byte[] s)    {
        if (s.length == 0)    {
            return 0L;
        } else {
            long h = 8486681283805525963L;
            for (int i = s.length - 1; i >= 0; i--)   {
                h = h * 2227328094050660801L + (s[i] & 0xFFL) * 5801442270845775361L;
            }
            return h;
        }
    }
}
