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
 */

package net.daporkchop.lib.nbt.util.object;

import lombok.NonNull;
import net.daporkchop.lib.nbt.tag.notch.CompoundTag;
import net.daporkchop.lib.nbt.tag.notch.ListTag;
import net.daporkchop.lib.nbt.tag.pork.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author DaPorkchop_
 */
public class NBTObjectWriter {
    /**
     * Write an object to an NBT compound tag
     * @param val the object to write
     * @param tag the tag to write to
     * @param <T> the type of the object to be written
     */
    public static <T> void write(@NonNull T val, @NonNull CompoundTag tag)  {
        try {
            doWriting(val, tag);
        } catch (Exception e)   {
            throw new RuntimeException(e);
        }
    }

    private static void doWriting(Object value, @NonNull CompoundTag tag) throws Exception  {
        if (value == null)  {
            tag.put("__null", new BooleanTag("__null", true));
            return;
        }
        Class clazz = value.getClass();
        tag.putString("__class", clazz.getCanonicalName());
        List<CompoundTag> list = new ArrayList<>();
        do {
            CompoundTag newTag = new CompoundTag(null);
            list.add(newTag);
            newTag.putString("__className", clazz.getCanonicalName());
            writeFieldsForClass(clazz, value, newTag);
        } while ((clazz = clazz.getSuperclass()) != null && clazz != Object.class);
        tag.putList("__fields", list);
    }

    private static void writeFieldsForClass(@NonNull Class clazz, @NonNull Object value, @NonNull CompoundTag tag) throws Exception  {
        for (Field field : clazz.getDeclaredFields())   {
            if (Modifier.isStatic(field.getModifiers()))    {
                continue;
            }
            field.setAccessible(true);
            Class fType = field.getType();
            if (fType == byte.class)    {
                tag.putByte(field.getName(), (byte) field.get(value));
            } else if (fType == short.class)    {
                tag.putShort(field.getName(), (short) field.get(value));
            } else if (fType == char.class)    {
                tag.put(field.getName(), new CharTag(field.getName(), (char) field.get(value)));
            } else if (fType == int.class)    {
                tag.putInt(field.getName(), (int) field.get(value));
            } else if (fType == long.class)    {
                tag.putLong(field.getName(), (long) field.get(value));
            } else if (fType == float.class)    {
                tag.putFloat(field.getName(), (float) field.get(value));
            } else if (fType == double.class)    {
                tag.putDouble(field.getName(), (double) field.get(value));
            } else if (fType == String.class)    {
                tag.putString(field.getName(), (String) field.get(value));
            } else if (fType.isArray()) {
                //TODO: multidimensional arrays currently are not supported
                if (fType == byte[].class)  {
                    tag.putByteArray(field.getName(), (byte[]) field.get(value));
                } else if (fType == short[].class)  {
                    tag.put(field.getName(), new ShortArrayTag(field.getName(), (short[]) field.get(value)));
                } else if (fType == char[].class)  {
                    tag.put(field.getName(), new CharArrayTag(field.getName(), (char[]) field.get(value)));
                } else if (fType == int[].class)  {
                    tag.putIntArray(field.getName(), (int[]) field.get(value));
                } else if (fType == long[].class)  {
                    tag.putLongArray(field.getName(), (long[]) field.get(value));
                } else if (fType == float[].class)  {
                    tag.put(field.getName(), new FloatArrayTag(field.getName(), (float[]) field.get(value)));
                } else if (fType == double[].class)  {
                    tag.put(field.getName(), new DoubleArrayTag(field.getName(), (double[]) field.get(value)));
                } else {
                    Object[] array = (Object[]) field.get(value);
                    List<CompoundTag> list = new ArrayList<>();
                    for (Object o : array)  {
                        CompoundTag newTag = new CompoundTag(null);
                        doWriting(o, newTag);
                        list.add(newTag);
                    }
                    tag.putList(field.getName(), list);
                }
            } else {
                CompoundTag newTag = new CompoundTag(field.getName());
                doWriting(field.get(value), newTag);
            }
        }
    }
}
