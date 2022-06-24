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

package net.daporkchop.lib.primitive.generator.replacer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.primitive.generator.Context;
import net.daporkchop.lib.primitive.generator.TokenReplacer;

import static net.daporkchop.lib.primitive.generator.param.primitive.Primitive.*;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class FileHeaderReplacer implements TokenReplacer {
    @Override
    public String replace(@NonNull Context context, @NonNull String text, String pkg) {
        switch (text) {
            case HEADERS_DEF: {
                StringBuilder builder = new StringBuilder();
                context.getConfig().getLicense().appendLicense(builder);

                if (builder.length() != 0) {
                    builder.append('\n');
                }
                builder.append(pkg).append("\n\n");

                int preImportsLength = builder.length();
                context.getConfig().getImports().appendImports(builder);
                if (builder.length() == preImportsLength) { //no imports were added, remove the trailing newlines we added before
                    builder.setLength(preImportsLength - 2);
                }
                return builder.toString();
            }
            case LICENSE_DEF: {
                StringBuilder builder = new StringBuilder();
                context.getConfig().getLicense().appendLicense(builder);
                return builder.toString();
            }
            case PACKAGE_DEF:
                return pkg;
            case IMPORTS_DEF: {
                StringBuilder builder = new StringBuilder();
                context.getConfig().getImports().appendImports(builder);
                return builder.toString();
            }
        }
        return null;
    }
}
