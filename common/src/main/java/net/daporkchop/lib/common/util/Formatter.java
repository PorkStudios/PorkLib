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

package net.daporkchop.lib.common.util;

import lombok.NonNull;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * A string formatter that doesn't create a new copy of the string for every parameter
 * <p>
 * Be aware that this does NOT have any extra features like e.g. adding leading zeroes to text, etc. It just
 * uses the result of {@link Object#toString()}.
 *
 * @author DaPorkchop_
 */
public class Formatter {
    private static final String[] REPLACEMENT_CACHE = new String[15];
    private static final ThreadLocal<Map<Integer, String[]>> KEY_CACHE = ThreadLocal.withInitial(WeakHashMap::new);
    private static final ThreadLocal<Map<Integer, String[]>> VALUE_CACHE = ThreadLocal.withInitial(WeakHashMap::new);
    private static final ThreadLocal<Map<Integer, int[]>> OFFSET_CACHE = ThreadLocal.withInitial(WeakHashMap::new);

    static {
        for (int i = Formatter.REPLACEMENT_CACHE.length - 1; i >= 0; i--) {
            Formatter.REPLACEMENT_CACHE[i] = String.format("${%d}", i);
        }
    }

    private static String getReplacementKey(int i) {
        if (i < 0) {
            throw new IllegalArgumentException(String.format("Index must be at least 0! (given: %d)", i));
        } else if (i < REPLACEMENT_CACHE.length) {
            return REPLACEMENT_CACHE[i];
        } else {
            return String.format("${%d}", i);
        }
    }

    private static int indexOf(int i, @NonNull int[] arr) {
        for (int j = 0; j < arr.length; j++) {
            if (arr[j] == i) {
                return j;
            }
        }
        return -1;
    }

    public static String format(@NonNull String message, @NonNull Object... params) {
        String[] values = VALUE_CACHE.get().computeIfAbsent(params.length, String[]::new);
        boolean reentrant = false;
        for (int i = params.length - 1; i >= 0; i--)    {
            if (values[i] != null)  {
                reentrant = true;
                break;
            }
        }
        if (reentrant)  {
            values = new String[params.length];
        }
        int valuesConsecutiveLength = 0;
        for (int i = params.length - 1; i >= 0; i--) {
            Object o = params[i];
            String s;
            if (o == null)  {
                s = "null";
            } else if (o instanceof String) {
                s = (String) o;
            } else if (o instanceof Class) {
                s = ((Class) o).getCanonicalName();
            } else if (o instanceof File)   {
                s = ((File) o).getAbsolutePath();
            } else {
                s = Objects.toString(o);
            }
            valuesConsecutiveLength += (values[i] = s).length();
        }
        String[] replacementKeys = reentrant ? new String[params.length] : KEY_CACHE.get().computeIfAbsent(params.length, String[]::new);
        int keysConsecutiveLength = 0;
        for (int i = params.length - 1; i >= 0; i--) {
            keysConsecutiveLength += (replacementKeys[i] = getReplacementKey(i)).length();
        }
        int[] offsets = reentrant ? new int[params.length] : OFFSET_CACHE.get().computeIfAbsent(params.length, int[]::new);
        for (int i = params.length - 1; i >= 0; i--) {
            if ((offsets[i] = message.indexOf(replacementKeys[i])) == -1) {
                //don't allocate too many chars
                valuesConsecutiveLength -= values[i].length();
                keysConsecutiveLength -= replacementKeys[i].length();
            }
        }
        char[] letters = new char[message.length() + valuesConsecutiveLength - keysConsecutiveLength];
        int i = 0;
        for (int j = 0; j < message.length(); j++) {
            int k = indexOf(j, offsets);
            if (k == -1) {
                letters[i++] = message.charAt(j);
            } else {
                j += replacementKeys[k].length() - 1;
                String replacement = values[k];
                for (int l = 0; l < replacement.length(); l++) {
                    letters[i++] = replacement.charAt(l);
                }
            }
        }
        Arrays.fill(values, null);
        Arrays.fill(replacementKeys, null);
        return PorkUtil.wrap(letters);
    }
}
