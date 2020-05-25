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

package net.daporkchop.lib.primitive.generator.option;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.primitive.generator.Primitive;
import net.daporkchop.lib.primitive.generator.TokenReplacer;

import java.util.List;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class ParameterContext {
    @NonNull
    private final Parameter parameter;
    @NonNull
    private final Primitive primitive;

    public String replace(@NonNull String token, boolean lowercase, @NonNull List<ParameterContext> params) {
        switch (token) {
            case "":
                return lowercase
                       ? this.primitive.generic ? this.parameter.genericName : this.primitive.name
                       : this.primitive.displayName;
            case "obj":
                return this.primitive.fullName;
            case "unsafe":
                return PorkUtil.fallbackIfNull(this.primitive.unsafeName, this.primitive.fullName);
            case "fullname":
                return this.primitive.generic ? this.parameter.genericName : this.primitive.fullName;
            case "name":
                return this.primitive.name;
            case "cast":
                return this.primitive.generic ? '(' + this.parameter.genericName + ") " : "";
            case "E":
                return this.primitive.emptyValue;
            case "nG":
                return this.primitive.generic ? "" : this.primitive.name;
            case "G":
                return this.primitive.generic ? '<' + this.parameter.genericName + '>' : "";
            case "arrOffset":
                return "PUnsafe.ARRAY_" + this.primitive.name.toUpperCase() + "_BASE_OFFSET";
            case "arrScale":
                return "PUnsafe.ARRAY_" + this.primitive.name.toUpperCase() + "_INDEX_SCALE";
        }
        throw new IllegalArgumentException(token);
    }

    @Override
    public int hashCode() {
        return this.parameter.hashCode() * 31 + this.primitive.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)    {
            return true;
        } else if (obj instanceof ParameterContext)    {
            ParameterContext other = (ParameterContext) obj;
            return this.parameter.equals(other.parameter) && this.primitive.equals(other.primitive);
        } else {
            return false;
        }
    }
}
