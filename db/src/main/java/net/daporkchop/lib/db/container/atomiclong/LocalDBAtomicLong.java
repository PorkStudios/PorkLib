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

package net.daporkchop.lib.db.container.atomiclong;

import lombok.Getter;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.db.Container;
import net.daporkchop.lib.db.container.AbstractContainer;
import net.daporkchop.lib.db.local.AbstractLocalContainer;
import net.daporkchop.lib.db.local.LocalContainer;
import net.daporkchop.lib.db.local.LocalDB;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author DaPorkchop_
 */
public class LocalDBAtomicLong extends AbstractLocalContainer<AtomicLong, LocalDBAtomicLong.Builder> implements DBAtomicLong<LocalDBAtomicLong, LocalDBAtomicLong.Builder, LocalDB> {
    @Getter
    private final AtomicLong value = new AtomicLong(0L);

    public LocalDBAtomicLong(Builder builder) throws IOException {
        super(builder);

        try (DataIn in = this.getIn("value", out -> {
            out.writeLong(0L);
        })) {
            this.value.set(in.readLong());
        }
    }

    @Override
    protected void doSave() throws IOException {
        try (DataOut out = this.getOut("value"))    {
            out.writeLong(this.value.get());
        }
    }

    public static class Builder extends AbstractLocalContainer.Builder<AtomicLong, LocalDBAtomicLong>  {
        protected Builder(LocalDB db, String name) {
            super(db, name);
        }

        @Override
        public LocalDBAtomicLong buildIfPresent() throws IOException {
            return null;
        }

        @Override
        protected LocalDBAtomicLong buildImpl() throws IOException {
            return null;
        }
    }
}
