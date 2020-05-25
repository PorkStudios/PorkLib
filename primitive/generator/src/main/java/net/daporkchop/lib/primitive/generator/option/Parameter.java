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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.function.PFunctions;
import net.daporkchop.lib.primitive.generator.Primitive;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * A single parameter for a template file.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class Parameter {
    /**
     * The generic parameter name of the parameter, when used
     */
    protected final String genericName;

    protected final int index;

    protected final Collection<Primitive> primitives;

    public Parameter(@NonNull JsonObject obj, int index) {
        checkArg(obj.has("genericName"), "genericName must be set!");
        this.genericName = obj.get("genericName").getAsString();
        this.index = notNegative(index, "index");
        if (obj.has("blacklist")) {
            checkState(!obj.has("whitelist"), "blacklist and whitelist may not be set together!");
            Set<Primitive> blacklist = StreamSupport.stream(obj.getAsJsonArray("blacklist").spliterator(), false)
                    .map(JsonElement::getAsString)
                    .map(Primitive.BY_NAME::get)
                    .peek(Objects::requireNonNull)
                    .collect(Collectors.toSet());
            this.primitives = Collections.unmodifiableList(Primitive.PRIMITIVES.stream()
                    .filter(PFunctions.not(blacklist::contains))
                    .collect(Collectors.toList()));
        } else if (obj.has("whitelist")) {
            checkState(!obj.has("blacklist"), "blacklist and whitelist may not be set together!");
            this.primitives = Collections.unmodifiableList(StreamSupport.stream(obj.getAsJsonArray("blacklist").spliterator(), false)
                    .map(JsonElement::getAsString)
                    .map(Primitive.BY_NAME::get)
                    .peek(Objects::requireNonNull)
                    .collect(Collectors.toList()));
        } else {
            this.primitives = Primitive.PRIMITIVES;
        }
    }

    @Override
    public int hashCode() {
        return this.genericName.hashCode() * 31 + this.index;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)    {
            return true;
        } else if (obj instanceof Parameter)    {
            Parameter other = (Parameter) obj;
            return this.index == other.index && this.genericName.equals(other.genericName);
        } else {
            return false;
        }
    }
}
