/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.config.attribute.Comment;
import net.daporkchop.lib.config.decoder.ConfigDecoder;
import net.daporkchop.lib.config.util.Element;
import net.daporkchop.lib.reflection.PField;
import net.daporkchop.lib.reflection.PReflection;
import net.daporkchop.lib.reflection.util.Type;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles loading of config and such
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class PConfig {
    @NonNull
    protected final ConfigDecoder decoder;

    @SuppressWarnings("unchecked")
    public <C> C load(@NonNull Class<C> clazz, @NonNull File file) throws IOException   {
        return this.load(PUnsafe.allocateInstance(clazz), file);
    }

    @SuppressWarnings("unchecked")
    public <C> C load(@NonNull Class<C> clazz, @NonNull DataIn in) throws IOException   {
        return this.load(PUnsafe.allocateInstance(clazz), in);
    }

    public <C> C load(@NonNull C obj, @NonNull File file) throws IOException {
        return this.load(obj, DataIn.wrap(file));
    }

    public <C> C load(@NonNull C obj, @NonNull DataIn in) throws IOException   {
        Element.ContainerElement root = this.decoder.decode(in);
        Config config = PReflection.getAnnotation(obj.getClass(), Config.class);
        if (config != null) {
            if (config.staticInstance())    {
                PReflection.setStatic(obj.getClass(), config.staticName(), obj);
            }
        }
        this.loadInto(obj, obj.getClass(), root);
        return obj;
    }

    protected void loadInto(@NonNull Object obj, @NonNull Class clazz, @NonNull Element.ContainerElement element)    {
        Map<String, PField> fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> (field.getModifiers() & Modifier.STATIC) == 0)
                .map(PField::of)
                .collect(Collectors.toMap(
                        field -> {
                            Config.Name name = field.getAnnotation(Config.Name.class);
                            return name == null ? field.getName() : name.value();
                        },
                        field -> field
                ));
        for (Map.Entry<String, Element> entry : element.getValue().entrySet())  {
            String name = entry.getKey();
            Element subElement = entry.getValue();
            PField field = fields.remove(name);
            if (field == null)  {
                System.err.printf("[WARNING] Couldn't find field in %s with name %s!\n", clazz.getCanonicalName(), name);
                continue;
            }
            Object val;
            if (subElement instanceof Element.ContainerElement) {
                if (field.getType() != Type.OBJECT) {
                    throw new IllegalStateException(String.format("Exception for field %s: field is %s but loaded a container!", name, field.getType()));
                }
                Config.Implementation implementation = field.getAnnotation(Config.Implementation.class);
                Class subClazz = implementation == null ? field.getClassType() : implementation.value();
                val = PUnsafe.allocateInstance(subClazz);
                this.loadInto(val, subClazz, (Element.ContainerElement) subElement.getAs());
            } else {
                val = subElement.getValue();
            }
            this.putValue(obj, field, subElement.getType(), val);
        }
        if (!fields.isEmpty())  {
            fields.entrySet().removeIf(entry -> {
                Config.Default def = entry.getValue().getAnnotation(Config.Default.class);
                if (def == null)    {
                    return false;
                } else {
                    Object val;
                    if (entry.getValue().getType().isObject())  {
                        String methodName = def.objectValue();
                        String[] split = methodName.split("#", 2);
                        try {
                            Class clazz1 = Class.forName(split[0]);
                            Method method = clazz1.getDeclaredMethod(split[1]);
                            if ((method.getModifiers() & Modifier.STATIC) == 0) {
                                throw new IllegalArgumentException(String.format("Not static: %s", methodName));
                            }
                            val = method.invoke(null);
                        } catch (ClassNotFoundException
                                | NoSuchMethodException
                                | IllegalAccessException
                                | InvocationTargetException e)  {
                            throw new RuntimeException(e);
                        }
                    } else {
                        switch (entry.getValue().getType()) {
                            case BOOLEAN: {
                                val = def.booleanValue();
                            }
                            break;
                            case BYTE: {
                                val = def.byteValue();
                            }
                            break;
                            case SHORT: {
                                val = def.shortValue();
                            }
                            break;
                            case INT: {
                                val = def.intValue();
                            }
                            break;
                            case LONG: {
                                val = def.longValue();
                            }
                            break;
                            case FLOAT: {
                                val = def.floatValue();
                            }
                            break;
                            case DOUBLE: {
                                val = def.doubleValue();
                            }
                            break;
                            case CHAR: {
                                val = def.charValue();
                            }
                            break;
                            default:
                                throw new IllegalStateException(String.format("Invalid type: %s", entry.getValue().getType()));
                        }
                    }
                    if (val != null){
                        this.putValue(obj, entry.getValue(), null, val);
                    }
                    return true;
                }
            });
            if (!fields.isEmpty())   {
                throw new IllegalStateException();
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void putValue(@NonNull Object obj, @NonNull PField field, Type type, @NonNull Object val) {
        if (type == null)   {
            type = Type.getMoreAccurateType(val.getClass(), false, true);
        }
        if (type.isObject())    {
            field.set(obj, val);
        } else {
            switch (type)   {
                case BOOLEAN: {
                    field.setBoolean(obj, (Boolean) val);
                }
                break;
                case BYTE: {
                    field.setByte(obj, (Byte) val);
                }
                break;
                case SHORT: {
                    field.setShort(obj, (Short) val);
                }
                break;
                case INT: {
                    field.setInt(obj, (Integer) val);
                }
                break;
                case LONG: {
                    field.setLong(obj, (Long) val);
                }
                break;
                case FLOAT: {
                    field.setFloat(obj, (Float) val);
                }
                break;
                case DOUBLE: {
                    field.setDouble(obj, (Double) val);
                }
                break;
                case CHAR: {
                    field.setChar(obj, (Character) val);
                }
                break;
                default:
                    throw new IllegalArgumentException(String.format("Invalid type: %s", type));
            }
        }
    }

    public void save(@NonNull Object obj, @NonNull OutputStream out) throws IOException    {
        this.save(obj, DataOut.wrap(out));
    }

    public void save(@NonNull Object obj, @NonNull File file) throws IOException    {
        this.save(obj, DataOut.wrap(file));
    }

    public void save(@NonNull Object obj, @NonNull DataOut out) throws IOException  {
        Element.ContainerElement root = Element.dummyContainer(null, null, null);
        this.saveRecursive(root, obj);
        this.decoder.encode(root, out);
    }

    protected void saveRecursive(@NonNull Element.ContainerElement container, @NonNull Object obj)  {
        Class<?> clazz = obj.getClass();
        Map<String, PField> fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> (field.getModifiers() & Modifier.STATIC) == 0)
                .map(PField::of)
                .collect(Collectors.toMap(
                        field -> {
                            Config.Name name = field.getAnnotation(Config.Name.class);
                            return name == null ? field.getName() : name.value();
                        },
                        field -> field
                ));
        for (Map.Entry<String, PField> entry : fields.entrySet())   {
            Config.Comment commentAnnotation = entry.getValue().getAnnotation(Config.Comment.class);
            Comment comment = Comment.from(commentAnnotation == null ? new String[0] : commentAnnotation.value());
            Element subElement = null;
            switch (entry.getValue().getType()) {
                case OBJECT: {
                    Object subObj = entry.getValue().get(obj);
                    if (subObj == null) {
                        continue;
                    } else {
                        if (subObj instanceof Number
                                || subObj instanceof String
                                || subObj instanceof Boolean
                                || subObj instanceof Character) {
                            subElement = Element.dummyElement(entry.getKey(), subObj, Type.getMoreAccurateType(subObj.getClass(), false, true), container, comment);
                        } else {
                            Element.ContainerElement sub = Element.dummyContainer(entry.getKey(), container, comment);
                            this.saveRecursive(sub, subObj);
                            subElement = sub;
                        }
                    }
                }
                break;
                case BOOLEAN: {
                    subElement = Element.dummyElement(entry.getKey(), entry.getValue().getBoolean(obj), Type.BOOLEAN, container, comment);
                }
                break;
                case BYTE: {
                    subElement = Element.dummyElement(entry.getKey(), entry.getValue().getByte(obj), Type.BYTE, container, comment);
                }
                break;
                case SHORT: {
                    subElement = Element.dummyElement(entry.getKey(), entry.getValue().getShort(obj), Type.SHORT, container, comment);
                }
                break;
                case INT: {
                    subElement = Element.dummyElement(entry.getKey(), entry.getValue().getInt(obj), Type.INT, container, comment);
                }
                break;
                case LONG: {
                    subElement = Element.dummyElement(entry.getKey(), entry.getValue().getLong(obj), Type.LONG, container, comment);
                }
                break;
                case FLOAT: {
                    subElement = Element.dummyElement(entry.getKey(), entry.getValue().getFloat(obj), Type.FLOAT, container, comment);
                }
                break;
                case DOUBLE: {
                    subElement = Element.dummyElement(entry.getKey(), entry.getValue().getDouble(obj), Type.DOUBLE, container, comment);
                }
                break;
                case CHAR: {
                    subElement = Element.dummyElement(entry.getKey(), entry.getValue().getChar(obj), Type.CHAR, container, comment);
                }
                break;
            }
            if (subElement == null) {
                throw new IllegalStateException(String.format("Unable to parse value in field: %s", entry.getValue().getName()));
            } else {
                container.setElement(entry.getKey(), subElement);
            }
        }
    }
}
