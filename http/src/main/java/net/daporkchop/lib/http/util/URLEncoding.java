/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package net.daporkchop.lib.http.util;

import io.netty.util.internal.StringUtil;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.misc.string.PStrings;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.encoding.Hexadecimal;
import net.daporkchop.lib.http.util.exception.HttpException;

import java.io.IOException;
import java.util.BitSet;

/**
 * Because the official Java URLEncoder is bad.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class URLEncoding {
    protected final BitSet OK_CHARS = new BitSet(128);

    static {
        OK_CHARS.set('a', 'z' + 1);
        OK_CHARS.set('A', 'Z' + 1);
        OK_CHARS.set('0', '9' + 1);
        OK_CHARS.set('*');
        OK_CHARS.set('-');
        OK_CHARS.set('.');
        OK_CHARS.set('_');
    }

    public String encode(@NonNull CharSequence text) {
        return encode(text, false);
    }

    public String encode(@NonNull CharSequence text, boolean preserveSlash) {
        try (Handle<StringBuilder> handle = PorkUtil.STRINGBUILDER_POOL.get()) {
            StringBuilder builder = handle.get();
            builder.setLength(0);
            encode(builder, text, preserveSlash);
            return builder.toString();
        } catch (IOException e) {
            //can't happen
            throw new IllegalStateException(e);
        }
    }

    public CharSequence encodeToCharSequence(@NonNull CharSequence text) {
        return encode(text, false);
    }

    public CharSequence encodeToCharSequence(@NonNull CharSequence text, boolean preserveSlash) {
        try {
            StringBuilder builder = new StringBuilder(text.length());
            encode(builder, text, preserveSlash);
            return builder;
        } catch (IOException e) {
            //can't happen
            throw new IllegalStateException(e);
        }
    }

    public void encode(@NonNull Appendable dst, @NonNull CharSequence text) throws IOException {
        encode(dst, text, false);
    }

    public void encode(@NonNull Appendable dst, @NonNull CharSequence text, boolean preserveSlash) throws IOException {
        char[] unwrapped = PStrings.tryCharSequenceToImmutableArray(text).orElse(null);
        if (unwrapped != null) {
            encodeFromCharArray(dst, unwrapped, preserveSlash);
        } else {
            encodeFromCharSequence(dst, text, preserveSlash);
        }
    }

    private void encodeFromCharArray(@NonNull Appendable dst, @NonNull char[] arr, boolean preserveSlash) throws IOException {
        for (int i = 0; i < arr.length; i++) {
            char c = arr[i];
            if (OK_CHARS.get(c) || (preserveSlash && c == '/')) {
                dst.append(c);
            } else if (c == ' ') {
                dst.append('+');
            } else {
                if (c < 0x80) {
                    Hexadecimal.encode(dst.append('%'), (byte) c);
                } else if (c < 0x800) {
                    Hexadecimal.encode(dst.append('%'), (byte) (0xC0 | (c >> 6)));
                    Hexadecimal.encode(dst.append('%'), (byte) (0x80 | (c & 0x3F)));
                } else if (StringUtil.isSurrogate(c)) {
                    if (Character.isHighSurrogate(c) && ++i < arr.length) {
                        char c2 = arr[i];
                        if (Character.isLowSurrogate(c2)) {
                            int codePoint = Character.toCodePoint(c, c2);
                            Hexadecimal.encode(dst.append('%'), (byte) (0xF0 | (codePoint >> 18)));
                            Hexadecimal.encode(dst.append('%'), (byte) (0x80 | ((codePoint >> 12) & 0x3F)));
                            Hexadecimal.encode(dst.append('%'), (byte) (0x80 | ((codePoint >> 6) & 0x3F)));
                            Hexadecimal.encode(dst.append('%'), (byte) (0x80 | (codePoint & 0x3F)));
                        } else {
                            Hexadecimal.encode(dst.append('%'), (byte) '?');
                            i--;
                        }
                    } else {
                        Hexadecimal.encode(dst.append('%'), (byte) '?');
                    }
                }
            }
        }
    }

    private void encodeFromCharSequence(@NonNull Appendable dst, @NonNull CharSequence text, boolean preserveSlash) throws IOException {
        //all the ternary operators should be optimized away by the JIT compiler
        // (i'd assume that it makes two copies of the method: one for arr == null and one for arr != null)

        for (int i = 0, length = text.length(); i < length; i++) {
            char c = text.charAt(i);
            if (OK_CHARS.get(c) || (preserveSlash && c == '/')) {
                dst.append(c);
            } else if (c == ' ') {
                dst.append('+');
            } else {
                if (c < 0x80) {
                    Hexadecimal.encode(dst.append('%'), (byte) c);
                } else if (c < 0x800) {
                    Hexadecimal.encode(dst.append('%'), (byte) (0xC0 | (c >> 6)));
                    Hexadecimal.encode(dst.append('%'), (byte) (0x80 | (c & 0x3F)));
                } else if (StringUtil.isSurrogate(c)) {
                    if (Character.isHighSurrogate(c) && ++i < length) {
                        char c2 = text.charAt(i);
                        if (Character.isLowSurrogate(c2)) {
                            int codePoint = Character.toCodePoint(c, c2);
                            Hexadecimal.encode(dst.append('%'), (byte) (0xF0 | (codePoint >> 18)));
                            Hexadecimal.encode(dst.append('%'), (byte) (0x80 | ((codePoint >> 12) & 0x3F)));
                            Hexadecimal.encode(dst.append('%'), (byte) (0x80 | ((codePoint >> 6) & 0x3F)));
                            Hexadecimal.encode(dst.append('%'), (byte) (0x80 | (codePoint & 0x3F)));
                        } else {
                            Hexadecimal.encode(dst.append('%'), (byte) '?');
                            i--;
                        }
                    } else {
                        Hexadecimal.encode(dst.append('%'), (byte) '?');
                    }
                }
            }
        }
    }

    public String decode(@NonNull CharSequence text) throws HttpException {
        try (Handle<StringBuilder> handle = PorkUtil.STRINGBUILDER_POOL.get()) {
            StringBuilder builder = handle.get();
            builder.setLength(0);
            decode(builder, text);
            return builder.toString();
        }
    }

    public CharSequence decodeToCharSequence(@NonNull CharSequence text) throws HttpException {
        StringBuilder builder = new StringBuilder();
        decode(builder, text);
        return builder;
    }

    public void decode(@NonNull StringBuilder to, @NonNull CharSequence text) throws HttpException {
        char[] unwrapped = PStrings.tryCharSequenceToImmutableArray(text).orElse(null);
        if (unwrapped != null) {
            decodeFromCharArray(to, unwrapped);
        } else {
            decodeFromCharSequence(to, text);
        }
    }

    private void decodeFromCharArray(@NonNull StringBuilder to, @NonNull char[] arr) throws HttpException {
        for (int i = 0; i < arr.length; i++) {
            char c = arr[i];
            if (c == '%') {
                i += 2;
                int b;
                if (i >= arr.length || (b = Hexadecimal.decodeUnsigned(arr[i - 1], arr[i])) < 0) {
                    throw StatusCodes.BAD_REQUEST.exception();
                }
                if ((b & 0xE0) == 0xC0) {
                    i += 3;
                    int b2;
                    if (i >= arr.length
                        || arr[i - 2] != '%'
                        || (b2 = Hexadecimal.decodeUnsigned(arr[i - 1], arr[i])) < 0) {
                        throw StatusCodes.BAD_REQUEST.exception();
                    }
                    to.append((char) (((b & 0x1F) << 6) | (b2 & 0x3F)));
                } else if ((b & 0xF0) == 0xF0) {
                    i += 9;
                    int b2, b3, b4;
                    if (i >= arr.length
                        || arr[i - 8] != '%'
                        || (b2 = Hexadecimal.decodeUnsigned(arr[i - 7], arr[i - 6])) < 0
                        || arr[i - 5] != '%'
                        || (b3 = Hexadecimal.decodeUnsigned(arr[i - 4], arr[i - 3])) < 0
                        || arr[i - 2] != '%'
                        || (b4 = Hexadecimal.decodeUnsigned(arr[i - 1], arr[i])) < 0) {
                        throw StatusCodes.BAD_REQUEST.exception();
                    }
                    to.appendCodePoint(((b & 0xF) << 18) | ((b2 & 0x3F) << 12) | ((b3 & 0x3F) << 6) | (b4 & 0x3F));
                } else {
                    to.append((char) b);
                }
            } else if (c == '+') {
                to.append(' ');
            } else {
                to.append(c);
            }
        }
    }

    private void decodeFromCharSequence(@NonNull StringBuilder to, @NonNull CharSequence text) throws HttpException {
        for (int i = 0, length = text.length(); i < length; i++) {
            char c = text.charAt(i);
            if (c == '%') {
                i += 2;
                int b;
                if (i >= length || (b = Hexadecimal.decodeUnsigned(text.charAt(i - 1), text.charAt(i))) < 0) {
                    throw StatusCodes.BAD_REQUEST.exception();
                }
                if ((b & 0xE0) == 0xC0) {
                    i += 3;
                    int b2;
                    if (i >= length
                        || text.charAt(i - 2) != '%'
                        || (b2 = Hexadecimal.decodeUnsigned(text.charAt(i - 1), text.charAt(i))) < 0) {
                        throw StatusCodes.BAD_REQUEST.exception();
                    }
                    to.append((char) (((b & 0x1F) << 6) | (b2 & 0x3F)));
                } else if ((b & 0xF0) == 0xF0) {
                    i += 9;
                    int b2, b3, b4;
                    if (i >= length
                        || text.charAt(i - 8) != '%'
                        || (b2 = Hexadecimal.decodeUnsigned(text.charAt(i - 7), text.charAt(i - 6))) < 0
                        || text.charAt(i - 5) != '%'
                        || (b3 = Hexadecimal.decodeUnsigned(text.charAt(i - 4), text.charAt(i - 3))) < 0
                        || text.charAt(i - 2) != '%'
                        || (b4 = Hexadecimal.decodeUnsigned(text.charAt(i - 1), text.charAt(i))) < 0) {
                        throw StatusCodes.BAD_REQUEST.exception();
                    }
                    to.appendCodePoint(((b & 0xF) << 18) | ((b2 & 0x3F) << 12) | ((b3 & 0x3F) << 6) | (b4 & 0x3F));
                } else {
                    to.append((char) b);
                }
            } else if (c == '+') {
                to.append(' ');
            } else {
                to.append(c);
            }
        }
    }
}
