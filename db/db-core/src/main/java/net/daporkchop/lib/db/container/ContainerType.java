/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.db.container;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.common.setting.OptionGroup;
import net.daporkchop.lib.common.setting.Settings;
import net.daporkchop.lib.db.engine.DBEngine;
import net.daporkchop.lib.db.container.map.DBMap;

import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
@Getter
public class ContainerType<C extends Container> {
    private static final Class<?>[] TYPES = {
            DBMap.class
    };

    public static final int TYPE_MAP = 0;

    protected final int id;
    protected final OptionGroup options;
    protected final Function<Settings, C> builder;
    protected final Class<C> clazz;
    protected final Class<? extends DBEngine> engineClazz;

    public ContainerType(int id, @NonNull Class<C> clazz, @NonNull Class<? extends DBEngine> engineClazz, @NonNull OptionGroup options, @NonNull Function<Settings, C> builder)  {
        if (id < 0 || id >= TYPES.length)    {
            throw new IllegalArgumentException(String.format("ID must be in range 0-%d!", TYPES.length));
        } else if (!TYPES[id].isAssignableFrom(clazz))  {
            throw new IllegalArgumentException(String.format("Class \"%s\" does not inherit from \"%s\"!", clazz.getCanonicalName(), TYPES[id].getCanonicalName()));
        }

        this.id = id;
        this.options = options;
        this.builder = builder;
        this.clazz = clazz;
        this.engineClazz = engineClazz;
    }

    public ContainerBuilder<C> builder()    {
        return new ContainerBuilder<>(Settings.builder().options(this.options).build(), this.builder);
    }
}
