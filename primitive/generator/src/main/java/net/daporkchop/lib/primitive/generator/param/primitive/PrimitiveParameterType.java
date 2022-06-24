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

import lombok.NonNull;
import net.daporkchop.lib.primitive.generator.param.Parameter;
import net.daporkchop.lib.primitive.generator.param.ParameterContext;
import net.daporkchop.lib.primitive.generator.param.ParameterType;
import net.daporkchop.lib.primitive.generator.param.ParameterValue;

import java.util.Map;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * @author DaPorkchop_
 */
public class PrimitiveParameterType implements ParameterType<PrimitiveParameterOptions> {
    @Override
    public Map<String, ParameterValue<PrimitiveParameterOptions>> getValuesByName() {
        return uncheckedCast(Primitive.BY_NAME);
    }

    @Override
    public Class<PrimitiveParameterOptions> optionsClass() {
        return PrimitiveParameterOptions.class;
    }

    @Override
    public PrimitiveParameterOptions defaultOptions() {
        throw new UnsupportedOperationException("cannot create default options for primitive parameter options!");
    }

    @Override
    public ParameterContext<PrimitiveParameterOptions> makeContext(@NonNull Parameter<PrimitiveParameterOptions> parameter, @NonNull ParameterValue<PrimitiveParameterOptions> value) {
        return new PrimitiveParameterContext(parameter, (Primitive) value);
    }
}
