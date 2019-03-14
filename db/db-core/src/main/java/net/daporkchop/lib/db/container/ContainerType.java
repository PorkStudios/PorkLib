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
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.setting.OptionGroup;
import net.daporkchop.lib.common.setting.Settings;
import net.daporkchop.lib.db.engine.DBEngine;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.db.engine.EngineContainerTypeInfo;

import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class ContainerType<C extends Container> {
    public static final int TYPE_COUNT = 1;

    public static final int TYPE_MAP = 0;

    public static final ContainerType<DBMap> MAP = new ContainerType<>(TYPE_MAP, DBMap.class);

    protected final int id;
    @NonNull
    protected final Class<C> clazz;

    @SuppressWarnings("unchecked")
    public <E extends DBEngine> ContainerBuilder<C, E> builder(@NonNull EngineContainerTypeInfo info, @NonNull E engine)    {
        return new ContainerBuilder<>(Settings.builder().options(info.getOptions(this)).build(), info.getBuilder(this), engine);
    }
}
