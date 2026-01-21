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
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.primitive.generator.config.GeneratorConfig;
import net.daporkchop.lib.primitive.generator.param.Parameter;
import net.daporkchop.lib.primitive.generator.param.ParameterContext;
import net.daporkchop.lib.primitive.generator.param.primitive.Primitive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * @author DaPorkchop_
 */
@Data
public final class Context {
    @NonNull
    private final Generator generator;
    @NonNull
    private final Optional<Context> parent;
    @NonNull
    private final Template template;
    @NonNull
    private final List<? extends ParameterContext<?>> params;

    @Getter(lazy = true)
    private final boolean isAnyParameterGeneric = this.params.stream().map(ParameterContext::value).filter(Primitive.class::isInstance).map(Primitive.class::cast).anyMatch(Primitive::isGeneric);

    @Getter(AccessLevel.NONE)
    private final Map<String, String> evaluatedMethods = new HashMap<>();

    public GeneratorConfig getConfig() {
        return this.template.getConfig();
    }

    public String evaluateMethod(@NonNull String _methodName) {
        return this.evaluatedMethods.computeIfAbsent(_methodName, methodName -> {
            Template methodTemplate = this.template.getMethods().get(methodName);
            checkArg(methodTemplate != null, "unknown method: '%s'", methodName);

            List<Parameter<?>> ownParameters = this.getConfig().getParmeters().getParameters();
            List<Parameter<?>> methodParameters = methodTemplate.getConfig().getParmeters().getParameters();
            checkState(ownParameters.size() < methodParameters.size(), "method '%s' must add at least one parameter!", methodName);
            checkState(ownParameters.equals(methodParameters.subList(0, ownParameters.size())), "method '%s' must start with the same parameters as its parent context");

            Stream<List<? extends ParameterContext<?>>> parametersStream = Stream.of(this.params);
            for (Parameter<?> parameter : methodParameters.subList(ownParameters.size(), methodParameters.size())) {
                parametersStream = parametersStream.flatMap(currentParams -> parameter.values().stream()
                        .map(value -> parameter.type().makeContext(uncheckedCast(parameter), uncheckedCast(value)))
                        .map(ctx -> {
                            List<ParameterContext<?>> params = new ArrayList<>(currentParams.size() + 1);
                            params.addAll(currentParams);
                            params.add(ctx);
                            return params;
                        }));
            }

            String content = parametersStream.map(methodParams -> new Context(this.generator, Optional.of(this), methodTemplate, Collections.unmodifiableList(methodParams)))
                    .map(methodContext -> this.generator.processString(methodTemplate.getTemplateFileContent(), methodContext))
                    .filter(text -> !text.trim().isEmpty())
                    .collect(Collectors.joining());

            if (!content.isEmpty()) {
                if (content.charAt(0) == '\n' || content.charAt(content.length() - 1) == '\n') { //strip newlines from beginning and end of string
                    int startIndex = 0;
                    do {
                        startIndex++;
                    } while (startIndex < content.length() && content.charAt(startIndex) == '\n');

                    int endIndex = content.length();
                    while (endIndex >= startIndex && content.charAt(endIndex - 1) == '\n') {
                        endIndex--;
                    }

                    content = content.substring(startIndex, endIndex);
                }
            }

            return content;
        });
    }
}
