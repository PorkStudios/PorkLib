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
