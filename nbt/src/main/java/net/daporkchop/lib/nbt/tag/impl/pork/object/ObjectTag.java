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

package net.daporkchop.lib.nbt.tag.impl.pork.object;

import net.daporkchop.lib.nbt.TagType;
import net.daporkchop.lib.nbt.stream.NBTInputStream;
import net.daporkchop.lib.nbt.stream.NBTOutputStream;
import net.daporkchop.lib.nbt.tag.Tag;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class ObjectTag<T> extends Tag<T> {
    private static final Map<String, SerializerContainer> SERIALIZERS = new Hashtable<>();
    private static final Map<Class, String> REGISTERED_CLASSES = new Hashtable<>();

    public ObjectTag(String name) {
        super(name);
    }

    public ObjectTag(String name, T value) {
        super(name, value);
    }

    public synchronized static <A> void registerSerializer(String key, ISerializer<A> serializer, IDeserializer<A> deserializer, Class<A> clazz) {
        if (key == null || "null".equals(key) || key.isEmpty())
            throw new IllegalArgumentException("Key may not be null or empty!");
        if (clazz == null) throw new IllegalArgumentException("Class may not be null!");
        if (serializer == null) throw new IllegalArgumentException("Serializer may not be null!");
        if (deserializer == null) throw new IllegalArgumentException("Deserializer may not be null!");

        if (SERIALIZERS.containsKey(key)) {
            throw new IllegalArgumentException("Serializer key " + key + " is already registered!");
        } else if (REGISTERED_CLASSES.containsKey(clazz)) {
            throw new IllegalArgumentException("Class " + clazz.getCanonicalName() + " is already registered under key " + REGISTERED_CLASSES.get(clazz) + "!");
        } else {
            SERIALIZERS.put(key, new SerializerContainer<>(serializer, deserializer));
            REGISTERED_CLASSES.put(clazz, key);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void write(NBTOutputStream dos) throws IOException {
        if (getValue() == null) {
            dos.writeUTF("null");
            return;
        } else {
            T value = getValue();
            String key = REGISTERED_CLASSES.get(value.getClass());
            if (key == null)
                throw new IllegalStateException("No serializer found for class: " + value.getClass().getCanonicalName());

            dos.writeUTF(key);
            SerializerContainer<T> container = (SerializerContainer<T>) SERIALIZERS.get(key);
            container.serialize(value, dos);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load(NBTInputStream dis) throws IOException {
        String key = dis.readUTF();
        if (key.equals("null")) {
            setValue(null);
            return;
        } else {
            SerializerContainer container = SERIALIZERS.get(key);
            if (container == null) throw new IllegalStateException("No deserializer found for class: " + key);
            setValue(((SerializerContainer<T>) container).deserialize(dis));
        }
    }

    @Override
    public String toString() {
        return "ObjectTag " + getName() + " (class=" + (getValue() == null ? "null" : getValue().getClass().getCanonicalName()) + ")";
    }

    @Override
    public TagType getType() {
        return TagType.TAG_OBJECT;
    }

    @Override
    public Tag copy() {
        return new ObjectTag<>(getName(), getValue());
    }
}
