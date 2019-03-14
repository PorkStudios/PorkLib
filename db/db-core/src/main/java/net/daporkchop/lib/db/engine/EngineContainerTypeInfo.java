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

package net.daporkchop.lib.db.engine;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.setting.OptionGroup;
import net.daporkchop.lib.common.setting.Settings;
import net.daporkchop.lib.db.container.Container;
import net.daporkchop.lib.db.container.ContainerBuilder;
import net.daporkchop.lib.db.container.ContainerType;

import java.util.function.Function;

import static net.daporkchop.lib.db.container.ContainerType.TYPE_COUNT;

/**
 * @author DaPorkchop_
 */
public class EngineContainerTypeInfo {
    protected final Node[] values = new Node[TYPE_COUNT];

    public <C extends Container> EngineContainerTypeInfo configure(@NonNull ContainerType<C> type, OptionGroup options, Function<Settings, C> builder, Class<C> clazz)  {
        this.values[type.getId()] = new Node<>(options, builder, clazz);
        return this;
    }

    public OptionGroup getOptions(@NonNull ContainerType type)  {
        return this.values[type.getId()].options;
    }

    @SuppressWarnings("unchecked")
    public <C extends Container> Function<Settings, C> getBuilder(@NonNull ContainerType<C> type)  {
        return this.values[type.getId()].builder;
    }

    @SuppressWarnings("unchecked")
    public <C extends Container> Class<? extends C> getClass(@NonNull ContainerType<C> type)  {
        return this.values[type.getId()].clazz;
    }

    @RequiredArgsConstructor
    @Getter
    protected static class Node<C extends Container> {
        @NonNull
        protected final OptionGroup options;
        @NonNull
        protected final Function<Settings, C> builder;
        @NonNull
        protected final Class<C> clazz;
    }
}
