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

package net.daporkchop.lib.network.protocol.encapsulated.session;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionAttributeMap;
import org.apache.mina.core.session.IoSessionDataStructureFactory;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionDataStructureFactory implements IoSessionDataStructureFactory {
    @Getter
    private static final SessionDataStructureFactory instance = new SessionDataStructureFactory();

    @Override
    public IoSessionAttributeMap getAttributeMap(IoSession session) {
        return new FasterAttributeMap();
    }

    @Override
    public WriteRequestQueue getWriteRequestQueue(IoSession session) {
        return new DefaultWriteRequestQueue();
    }

    private static class FasterAttributeMap implements IoSessionAttributeMap {
        private final ConcurrentHashMap<Object, Object> attributes = new ConcurrentHashMap<>(4);
        private final Map<SessionData, Object> data = new EnumMap<>(SessionData.class);

        {
            this.data.put(SessionData.CONNECTION_STATE, ConnectionState.NONE);
        }

        @Override
        public Object getAttribute(IoSession session, Object key, Object defaultValue) {
            if (key == null) {
                throw new NullPointerException();
            }

            if (key instanceof SessionData) {
                if (defaultValue == null) {
                    return this.data.get(key);
                }
                Object o = this.data.putIfAbsent((SessionData) key, defaultValue);
                if (o == null) {
                    return defaultValue;
                } else {
                    return o;
                }
            }

            if (defaultValue == null) {
                return attributes.get(key);
            }

            Object object = attributes.putIfAbsent(key, defaultValue);

            if (object == null) {
                return defaultValue;
            } else {
                return object;
            }
        }


        @Override
        public Object setAttribute(IoSession session, Object key, Object value) {
            if (key == null) {
                throw new NullPointerException();
            }

            if (key instanceof SessionData) {
                if (value == null) {
                    return this.data.remove(key);
                }
                return this.data.put((SessionData) key, value);
            }

            if (value == null) {
                return attributes.remove(key);
            }

            return attributes.put(key, value);
        }


        @Override
        public Object setAttributeIfAbsent(IoSession session, Object key, Object value) {
            if (key == null) {
                throw new IllegalArgumentException("key");
            }

            if (value == null) {
                return null;
            }

            return attributes.putIfAbsent(key, value);
        }


        @Override
        public Object removeAttribute(IoSession session, Object key) {
            if (key == null) {
                throw new IllegalArgumentException("key");
            }

            return attributes.remove(key);
        }


        @Override
        public boolean removeAttribute(IoSession session, Object key, Object value) {
            if (key == null) {
                throw new IllegalArgumentException("key");
            }

            if (value == null) {
                return false;
            }

            try {
                return attributes.remove(key, value);
            } catch (NullPointerException e) {
                return false;
            }
        }


        @Override
        public boolean replaceAttribute(IoSession session, Object key, Object oldValue, Object newValue) {
            try {
                return attributes.replace(key, oldValue, newValue);
            } catch (NullPointerException e) {
            }

            return false;
        }


        @Override
        public boolean containsAttribute(IoSession session, Object key) {
            return attributes.containsKey(key);
        }


        @Override
        public Set<Object> getAttributeKeys(IoSession session) {
            synchronized (attributes) {
                return new HashSet<>(attributes.keySet());
            }
        }


        @Override
        public void dispose(IoSession session) {
            // Do nothing
        }
    }

    private static class DefaultWriteRequestQueue implements WriteRequestQueue {
        /**
         * A queue to store incoming write requests
         */
        private final Queue<WriteRequest> q = new ConcurrentLinkedQueue<>();


        @Override
        public void dispose(IoSession session) {
            // Do nothing
        }


        @Override
        public void clear(IoSession session) {
            q.clear();
        }


        @Override
        public boolean isEmpty(IoSession session) {
            return q.isEmpty();
        }


        @Override
        public void offer(IoSession session, WriteRequest writeRequest) {
            q.offer(writeRequest);
        }


        @Override
        public WriteRequest poll(IoSession session) {
            WriteRequest answer = q.poll();

            if (answer == AbstractIoSession.CLOSE_REQUEST) {
                session.closeNow();
                dispose(session);
                answer = null;
            }

            return answer;
        }


        @Override
        public String toString() {
            return q.toString();
        }


        @Override
        public int size() {
            return q.size();
        }
    }
}
