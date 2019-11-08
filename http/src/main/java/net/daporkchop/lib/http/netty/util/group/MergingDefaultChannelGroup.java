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

package net.daporkchop.lib.http.netty.util.group;

import io.netty.channel.Channel;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutor;
import lombok.NonNull;
import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * A wrapper on top of {@link DefaultChannelGroup} which additionally adds all channels added to itself to a specified shared
 * group.
 *
 * @author DaPorkchop_
 */
public class MergingDefaultChannelGroup extends DefaultChannelGroup {
    protected static final long DEFAULTCHANNELGROUP_EXECUTOR_OFFSET = PUnsafe.pork_getOffset(DefaultChannelGroup.class, "executor");

    protected final DefaultChannelGroup delegate;

    public MergingDefaultChannelGroup(@NonNull DefaultChannelGroup delegate) {
        super(PUnsafe.getObject(delegate, DEFAULTCHANNELGROUP_EXECUTOR_OFFSET));

        this.delegate = delegate;
    }

    public MergingDefaultChannelGroup(@NonNull DefaultChannelGroup delegate, EventExecutor executor) {
        super(executor);

        this.delegate = delegate;
    }

    public MergingDefaultChannelGroup(@NonNull DefaultChannelGroup delegate, String name, EventExecutor executor) {
        super(name, executor);

        this.delegate = delegate;
    }

    public MergingDefaultChannelGroup(@NonNull DefaultChannelGroup delegate, EventExecutor executor, boolean stayClosed) {
        super(executor, stayClosed);

        this.delegate = delegate;
    }

    public MergingDefaultChannelGroup(@NonNull DefaultChannelGroup delegate, String name, EventExecutor executor, boolean stayClosed) {
        super(name, executor, stayClosed);

        this.delegate = delegate;
    }

    @Override
    public boolean add(Channel channel) {
        return this.delegate.add(channel) & super.add(channel);
    }

    @Override
    public boolean remove(Object o) {
        return this.delegate.remove(o) & super.remove(o);
    }

    @Override
    public void clear() {
        this.delegate.removeAll(this);
        super.clear();
    }
}
