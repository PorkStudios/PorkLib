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

package net.daporkchop.lib.primitive.generator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.primitive.generator.option.Parameter;
import net.daporkchop.lib.primitive.generator.option.ParameterContext;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Accessors(chain = true)
public enum Primitive {
    BOOLEAN("Boolean", "Bool", "Boolean", "boolean", "false", false),
    BYTE("Byte", "Byte", "Byte", "byte", "(byte) -1", false),
    SHORT("Short", "Short", "Short", "short", "(short) -1", false),
    CHAR("Character", "Char", "Char", "char", "(char) 0", false),
    INT("Integer", "Int", "Int", "int", "-1", false),
    LONG("Long", "Long", "Long", "long", "-1L", false),
    FLOAT("Float", "Float", "Float", "float", "Float.NaN", false),
    DOUBLE("Double", "Double", "Double", "double", "Double.NaN", false),
    OBJECT("Object", "Obj", "Object", "Object", "null", true),
    ;

    public static final Set<Primitive> PRIMITIVES = Collections.unmodifiableSet(EnumSet.allOf(Primitive.class));

    public static final Map<String, Primitive> BY_NAME = Collections.unmodifiableMap(PRIMITIVES.stream()
            .collect(Collectors.toMap(Primitive::getName, Function.identity())));

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
    public final String fullName;
    @NonNull
    public final String displayName;
    @NonNull
    public final String unsafeName;
    @NonNull
    public final String name;
    @NonNull
    public final String emptyValue;
    public final boolean generic;

    @Override
    public String toString() {
        return this.fullName;
    }
}
