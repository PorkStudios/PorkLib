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

package net.daporkchop.lib.primitive.generator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.function.PFunctions;
import net.daporkchop.lib.primitive.generator.option.Parameter;
import net.daporkchop.lib.primitive.generator.option.ParameterContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Primitive {
    public static final Collection<Primitive> PRIMITIVES = Collections.unmodifiableList(Arrays.asList(
            new Primitive()
                    .setFullName("Boolean")
                    .setDisplayName("Bool")
                    .setName("boolean")
                    .setEmptyValue("false")
                    .build(),
            new Primitive()
                    .setFullName("Byte")
                    .setName("byte")
                    .setEmptyValue("(byte) -1")
                    .build(),
            new Primitive()
                    .setFullName("Short")
                    .setName("short")
                    .setEmptyValue("(short) -1")
                    .build(),
            new Primitive()
                    .setFullName("Character")
                    .setDisplayName("Char")
                    .setUnsafeName("Char")
                    .setName("char")
                    .setEmptyValue("(char) 0")
                    .build(),
            new Primitive()
                    .setFullName("Integer")
                    .setDisplayName("Int")
                    .setUnsafeName("Int")
                    .setName("int")
                    .setEmptyValue("-1")
                    .build(),
            new Primitive()
                    .setFullName("Long")
                    .setName("long")
                    .setEmptyValue("-1L")
                    .build(),
            new Primitive()
                    .setFullName("Float")
                    .setName("float")
                    .setEmptyValue("Float.NaN")
                    .build(),
            new Primitive()
                    .setFullName("Double")
                    .setName("double")
                    .setEmptyValue("Double.NaN")
                    .build(),
            new Primitive()
                    .setFullName("Object")
                    .setDisplayName("Obj")
                    .setName("Object")
                    .setGeneric()
                    .setEmptyValue("null")
                    .build()));

    public static final Map<String, Primitive> BY_NAME = Collections.unmodifiableMap(PRIMITIVES.stream()
            .collect(Collectors.toMap(Primitive::getName, PFunctions.identity())));

    public static final String PARAM_DEF = "P%d";
    public static final String DISPLAYNAME_DEF = String.format("_%s_", PARAM_DEF);
    public static final String BOXED_FORCE_DEF = String.format("_obj%s_", PARAM_DEF);
    public static final String UNSAFE_FORCE_DEF = String.format("_unsafe%s_", PARAM_DEF);
    public static final String FULLNAME_FORCE_DEF = String.format("_fullname%s_", PARAM_DEF);
    public static final String NAME_DEF = String.format("_%s_", PARAM_DEF.toLowerCase());
    public static final String NAME_FORCE_DEF = String.format("_name%s_", PARAM_DEF);
    public static final String HASHCODE_DEF = String.format("_hashCode%s_", PARAM_DEF);
    public static final String EQUALS_DEF = String.format("_equals%s_", PARAM_DEF);
    public static final String CAST_DEF = String.format("_cast%s_", PARAM_DEF);
    public static final String EMPTYVALUE_DEF = String.format("_E%s_", PARAM_DEF);
    public static final String NON_GENERIC_DEF = String.format("_nG%s_", PARAM_DEF);
    public static final String GENERIC_DEF = String.format("_G%s_", PARAM_DEF);
    public static final String GENERIC_EXTENDS_P_DEF = String.format("_Gextends%s_", PARAM_DEF);
    public static final String GENERIC_SUPER_P_DEF = String.format("_Gsuper%s_", PARAM_DEF);

    public static final String GENERIC_HEADER_DEF = "_gH_";
    public static final String GENERIC_EXTRA_DEF = "_G(extends|super)_";

    public static final String HEADERS_DEF = "_headers_";
    public static final String LICENSE_DEF = "_copyright_";
    public static final String PACKAGE_DEF = "_package_";
    public static final String IMPORTS_DEF = "_imports_";

    public static final String METHODS_DEF = "_methods_";

    public static final String UNSAFE_ARRAY_OFFSET_DEF = String.format("_arrOffset%s_", PARAM_DEF);
    public static final String UNSAFE_ARRAY_SCALE_DEF = String.format("_arrScale%s_", PARAM_DEF);

    public static String getGenericHeader(@NonNull List<ParameterContext> params, @NonNull String prefix) {
        List<ParameterContext> generics = params.stream().filter(ctx -> ctx.primitive().generic).collect(Collectors.toList());
        return generics.isEmpty() ? "" : generics.stream().map(ParameterContext::parameter).map(Parameter::genericName)
                .collect(Collectors.joining(", " + prefix, "<" + prefix, ">"));
    }

    @NonNull
    public String fullName;
    @NonNull
    public String displayName;
    @NonNull
    public String unsafeName;
    @NonNull
    public String name;
    @NonNull
    public String emptyValue;
    public boolean generic;

    public Primitive setGeneric() {
        this.generic = true;
        return this;
    }

    public Primitive build() {
        if (this.displayName == null) {
            this.displayName = this.fullName;
        }
        return this;
    }

    @Override
    public String toString() {
        return this.fullName;
    }
}
