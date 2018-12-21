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

import lombok.NonNull;
import net.daporkchop.lib.logging.Logging;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public class Parameters implements Logging {
    private final Map<String, Parameter> map = new HashMap<>();

    public Parameters(@NonNull List<String> entries, @NonNull ParameterRegistry registry)   {
        for (String entry : entries)    {
            int off = entry.indexOf(": ");
            if (off == -1)  {
                throw this.exception("Invalid entry: ${0}", entry);
            }
            String name = entry.substring(0, off);
            this.map.put(name, registry.parse(name, entry.substring(off + 2, entry.length())));
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Parameter<T> getParam(@NonNull String name)  {
        return (Parameter<T>) this.map.get(name);
    }

    public <T> T getValue(@NonNull String name) {
        Parameter<T> param = this.getParam(name);
        return param == null ? null : param.getValue();
    }

    public void forEach(@NonNull BiConsumer<String, Parameter> func)    {
        this.map.forEach(func);
    }

    public void forEach(@NonNull Consumer<Parameter> func)  {
        this.map.forEach((name, parameter) -> func.accept(parameter));
    }
}
