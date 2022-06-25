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

package net.daporkchop.lib.primitive.generator.param.custom;

import lombok.Data;
import lombok.NonNull;
import net.daporkchop.lib.primitive.generator.param.Parameter;
import net.daporkchop.lib.primitive.generator.param.ParameterContext;
import net.daporkchop.lib.primitive.generator.param.ParameterType;
import net.daporkchop.lib.primitive.generator.param.ParameterValue;

import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Data
public class CustomParameterType implements ParameterType<CustomParameterOptions> {
    @NonNull
    private final Map<String, CustomParameterValue> valuesByName;

    @Override
    public Class<CustomParameterOptions> optionsClass() {
        return CustomParameterOptions.class;
    }

    @Override
    public CustomParameterOptions defaultOptions() {
        return new CustomParameterOptions();
    }

    @Override
    public ParameterContext<CustomParameterOptions> makeContext(@NonNull Parameter<CustomParameterOptions> parameter, @NonNull ParameterValue<CustomParameterOptions> value) {
        return new CustomParameterContext(parameter, (CustomParameterValue) value);
    }
}
