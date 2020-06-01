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

package net.daporkchop.lib.primitive.generator.replacer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.primitive.generator.Generator;
import net.daporkchop.lib.primitive.generator.TokenReplacer;
import net.daporkchop.lib.primitive.generator.option.ParameterContext;

import java.util.List;

import static net.daporkchop.lib.primitive.generator.Primitive.*;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class FileHeaderReplacer implements TokenReplacer {
    @NonNull
    private final String imports;

    @Override
    public String replace(@NonNull String text, @NonNull List<ParameterContext> params, String pkg) {
        switch (text) {
            case HEADERS_DEF:
                return this.imports.isEmpty()
                       ? Generator.LICENSE + "\n\n" + pkg
                       : Generator.LICENSE + "\n\n" + pkg + "\n\n" + this.imports;
            case LICENSE_DEF:
                return Generator.LICENSE;
            case PACKAGE_DEF:
                return pkg;
            case IMPORTS_DEF:
                return this.imports;
        }
        return null;
    }
}
