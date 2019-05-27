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

package net.daporkchop.lib.concurrent.util;

import lombok.NonNull;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public abstract class DefaultListenable<I extends Listenable<I>> extends DefaultMutexHolder implements Listenable<I> {
    private volatile Object listener;

    @Override
    @SuppressWarnings("unchecked")
    public final I addListener(@NonNull Consumer<I> listener) {
        synchronized (this.mutex)   {
            Object curr = this.listener; //cache to avoid multiple volatile reads, doesn't matter since it's synchronized
            if (curr == this.mutex)    {
                this.doFireListener(listener); //listeners already fired!
            } else if (curr == null)    {
                this.listener = listener;
            } else if (curr instanceof Consumer)    {
                List list = new LinkedList();
                list.add(curr);
                list.add(listener);
                this.listener = list;
            } else if (curr instanceof List)    {
                ((List) curr).add(listener);
            } else {
                throw new IllegalStateException();
            }
        }
        return (I) this;
    }

    @SuppressWarnings("unchecked")
    protected final void fireListeners()    {
        synchronized (this.mutex)   {
            Object curr = this.listener;
            if (curr instanceof Consumer)  {
                this.doFireListener((Consumer<I>) curr);
            } else if (curr instanceof List)    {
                List<Consumer<I>> list = (List<Consumer<I>>) curr;
                for (int len = list.size(), i = 0; i < len; i++)    {
                    this.doFireListener(list.get(i));
                }
            }
            this.listener = this.mutex;
        }
    }

    protected abstract void doFireListener(@NonNull Consumer<I> listener);
}
