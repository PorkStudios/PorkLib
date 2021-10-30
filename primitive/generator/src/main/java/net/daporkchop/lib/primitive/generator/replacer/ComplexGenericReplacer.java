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

package net.daporkchop.lib.primitive.generator.replacer;

import lombok.NonNull;
import net.daporkchop.lib.common.reference.cache.Cached;
import net.daporkchop.lib.primitive.generator.TokenReplacer;
import net.daporkchop.lib.primitive.generator.option.ParameterContext;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
public class ComplexGenericReplacer implements TokenReplacer {
    public static final Cached<Matcher> COMPLEX_GENERIC_MATCHER = Cached.regex(Pattern.compile("^_G(?:\\d+(?:extends|super)?)+_$"));
    public static final Cached<Matcher> COMPLEX_GENERIC_PARAMS = Cached.regex(Pattern.compile("(\\d+)(extends|super)?"));

    @Override
    public String replace(@NonNull String text, @NonNull List<ParameterContext> params, String pkg) {
        Matcher matcher = COMPLEX_GENERIC_MATCHER.get().reset(text);
        if (matcher.matches()) {
            List<String> formatted = new ArrayList<>();
            matcher = COMPLEX_GENERIC_PARAMS.get().reset(text);
            while (matcher.find()) {
                ParameterContext param = params.get(Integer.parseUnsignedInt(matcher.group(1)));
                if (param.primitive().isGeneric()) {
                    String requirement = matcher.group(2);
                    formatted.add((requirement == null ? "" : "? " + requirement + ' ') + param.parameter().genericName());
                }
            }
            return formatted.isEmpty() ? "" : formatted.stream().collect(Collectors.joining(", ", "<", ">"));
        }
        return null;
    }
}
