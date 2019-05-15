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

package net.daporkchop.lib.network.pipeline;

import lombok.NonNull;
import net.daporkchop.lib.network.pipeline.event.PipelineHandler;
import net.daporkchop.lib.network.session.AbstractUserSession;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author DaPorkchop_
 */
public class Pipeline<S extends AbstractUserSession<S>> implements PipelineHandler.Firing<S> {
    protected final Head<S> head;
    protected final Tail<S> tail;

    protected final Lock readLock; //less pointer chasing!
    protected final Lock writeLock;

    public Pipeline(@NonNull Filter<S> head, @NonNull Filter<S> tail)   {
        this(new Head<>(head), new Tail<>(tail));
    }

    public Pipeline(@NonNull Head<S> head, @NonNull Tail<S> tail)   {
        this.head = head;
        this.tail = tail;

        ReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    @Override
    public void fireSessionOpened(@NonNull S session) {
        this.readLock.lock();
        try {
            this.head.fireSessionOpened(session);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void fireSessionClosed(@NonNull S session) {
        this.readLock.lock();
        try {
            this.head.fireSessionClosed(session);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void fireExceptionCaught(@NonNull S session, @NonNull Throwable t) {
        this.readLock.lock();
        try {
            this.head.fireExceptionCaught(session, t);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void fireMessageReceived(@NonNull S session, @NonNull Object msg, int channel) {
        this.readLock.lock();
        try {
            this.head.fireMessageReceived(session, msg, channel);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void fireMessageSent(@NonNull S session, @NonNull Object msg, int channel) {
        this.readLock.lock();
        try {
            this.tail.fireMessageSent(session, msg, channel);
        } finally {
            this.readLock.unlock();
        }
    }

    public Pipeline<S> addFirst(@NonNull PipelineHandler<S> handler)   {
        return this.addFirst(handler.toString(), handler);
    }

    public Pipeline<S> addFirst(@NonNull String name, @NonNull PipelineHandler<S> handler)   {
        this.writeLock.lock();
        try {
            this.insertBetween(this.head.prev, this.head, new Node<>(name, handler));
            return this;
        } finally {
            this.writeLock.unlock();
        }
    }

    public Pipeline<S> addLast(@NonNull PipelineHandler<S> handler)   {
        return this.addLast(handler.toString(), handler);
    }

    public Pipeline<S> addLast(@NonNull String name, @NonNull PipelineHandler<S> handler)   {
        this.writeLock.lock();
        try {
            this.insertBetween(this.tail, this.tail.next, new Node<>(name, handler));
            return this;
        } finally {
            this.writeLock.unlock();
        }
    }

    public Pipeline<S> replace(@NonNull String name, @NonNull PipelineHandler<S> handler)   {
        this.writeLock.lock();
        try {
            Node<S> toAdd = new Node<>(name, handler);
            this.forEachNode(old -> {
                if (name.equals(old.name))  {
                    old.prev.next = toAdd;
                    old.next.prev = toAdd;
                    toAdd.next = old.next;
                    toAdd.prev = old.prev;
                    return false;
                } else {
                    return true;
                }
            });
            if (toAdd.next == null) {
                throw new IllegalArgumentException(String.format("Unable to find node with name: \"%s\"", name));
            } else {
                this.update();
                return this;
            }
        } finally {
            this.writeLock.unlock();
        }
    }

    public Pipeline<S> remove(@NonNull String name)   {
        this.writeLock.lock();
        try {
            AtomicBoolean flag = new AtomicBoolean(false);
            this.forEachNode(old -> {
                if (name.equals(old.name))  {
                    old.prev.next = old.next;
                    old.next.prev = old.prev;
                    flag.set(true);
                    return false;
                } else {
                    return true;
                }
            });
            if (!flag.get()) {
                throw new IllegalArgumentException(String.format("Unable to find node with name: \"%s\"", name));
            } else {
                this.update();
                return this;
            }
        } finally {
            this.writeLock.unlock();
        }
    }

    protected void insertBetween(@NonNull Node<S> first, @NonNull Node<S> second, @NonNull Node<S> toAdd)    {
        this.writeLock.lock();
        try {
            this.checkInsert(toAdd.name);
            if (first.next != second || second.prev != first)   {
                throw new IllegalStateException();
            }

            first.next = toAdd;
            toAdd.next = second;

            second.prev = toAdd;
            toAdd.prev = first;

            this.update();
        } finally {
            this.writeLock.unlock();
        }
    }

    protected void checkInsert(@NonNull String name)    {
        this.forEachNode(node -> {
            if (name.equals(node.name)) {
                throw new IllegalArgumentException(String.format("Cannot insert node with duplicate name: \"%s\"!", name));
            }
        });
    }

    protected void update()  {
        this.writeLock.lock();
        try {
            Node<S> node = this.head;
            do {
                node.updateRelations();
            } while (!(node instanceof Tail) && (node = node.next) != null);
            node = this.head;
            do {
                node.updateSelf();
            } while (!(node instanceof Tail) && (node = node.next) != null);
        } finally {
            this.writeLock.unlock();
        }
    }

    protected void forEachNode(@NonNull Consumer<Node<S>> consumer) {
        this.readLock.lock();
        try {
            Node<S> node = this.head;
            do {
                consumer.accept(node);
            } while (!(node instanceof Tail) && (node = node.next) != null);
        } finally {
            this.readLock.unlock();
        }
    }

    protected void forEachNode(@NonNull Predicate<Node<S>> condition) {
        this.readLock.lock();
        try {
            Node<S> node = this.head;
            while (condition.test(node) && !(node instanceof Tail) && (node = node.next) != null);
        } finally {
            this.readLock.unlock();
        }
    }
}
