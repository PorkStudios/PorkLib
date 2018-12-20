/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.http.parameter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.parameter.def.ParameterAcceptEncoding;
import net.daporkchop.lib.http.parameter.def.ParameterHost;
import net.daporkchop.lib.logging.Logging;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class ParameterRegistry implements Logging {
    public static ParameterRegistry def()   {
        return new Builder()
                .register("Accept-Encoding", ParameterAcceptEncoding::new)
                .register("Host", ParameterHost::new)
                .build();
    }

    @NonNull
    private final Map<String, Function<String, Parameter>> parsers;

    public Parameter parse(@NonNull String name, @NonNull String content)   {
        Function<String, Parameter> func = this.parsers.get(name);
        if (func == null)   {
            return new Parameter.Simple(name, content);
        } else {
            Parameter parameter = func.apply(content);
            if (parameter == null)  {
                throw this.exception("Couldn't parse parameter: \"${0}: ${1}\"", name, content);
            } else {
                assert parameter.getName().equals(name);
                return parameter;
            }
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Builder {
        private final Map<String, Function<String, Parameter>> parsers = new HashMap<>();

        public Builder register(@NonNull String name, @NonNull Function<String, Parameter> func)    {
            this.parsers.put(name, func);
            return this;
        }

        public ParameterRegistry build()    {
            return new ParameterRegistry(this.parsers);
        }
    }
}
