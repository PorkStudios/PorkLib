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

package net.daporkchop.lib.primitive.generator.param.primitive;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.primitive.generator.Context;
import net.daporkchop.lib.primitive.generator.param.Parameter;
import net.daporkchop.lib.primitive.generator.param.ParameterContext;

/**
 * @author DaPorkchop_
 */
@Data
@Accessors(fluent = true)
public class PrimitiveParameterContext implements ParameterContext<PrimitiveParameterOptions> {
    @NonNull
    private final Parameter<PrimitiveParameterOptions> parameter;
    @NonNull
    private final Primitive value;

    @Override
    public String replace(@NonNull Context context, @NonNull String token, boolean lowercase) {
        switch (token) {
            case "":
                return lowercase
                        ? this.value.generic ? this.parameter.options().genericName() : this.value.name
                        : this.value.displayName;
            case "obj":
                return this.value.fullName;
            case "unsafe":
                return PorkUtil.fallbackIfNull(this.value.unsafeName, this.value.fullName);
            case "fullname":
                return this.value.generic ? this.parameter.options().genericName() : this.value.fullName;
            case "name":
                return this.value.name;
            case "cast":
                return this.value.generic ? '(' + this.parameter.options().genericName() + ") " : "";
            case "E":
                return this.value.emptyValue;
            case "nG":
                return this.value.generic ? "" : this.value.name;
            case "G":
                return this.value.generic ? '<' + this.parameter.options().genericName() + '>' : "";
            case "Gsuper":
                return this.value.generic ? "<? super " + this.parameter.options().genericName() + '>' : "";
            case "Gextends":
                return this.value.generic ? "<? extends " + this.parameter.options().genericName() + '>' : "";
            case "arrOffset":
                return "PUnsafe.ARRAY_" + this.value.name.toUpperCase() + "_BASE_OFFSET";
            case "arrScale":
                return "PUnsafe.ARRAY_" + this.value.name.toUpperCase() + "_INDEX_SCALE";
        }
        throw new IllegalArgumentException(token);
    }

    @Override
    public String toString() {
        return "primitive=" + this.value.name;
    }
}
