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

package net.daporkchop.lib.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.util.PConstants;
import net.daporkchop.lib.config.decoder.ConfigDecoder;
import net.daporkchop.lib.config.util.Element;
import net.daporkchop.lib.reflection.PField;
import net.daporkchop.lib.reflection.PReflection;
import net.daporkchop.lib.reflection.util.Type;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static net.daporkchop.lib.common.util.PUnsafe.*;

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
    public <C> C loadConfig(@NonNull Class<C> clazz, @NonNull File file) throws IOException   {
        return this.loadConfig((C) allocateInstance(clazz), file);
    }

    @SuppressWarnings("unchecked")
    public <C> C loadConfig(@NonNull Class<C> clazz, @NonNull DataIn in) throws IOException   {
        return this.loadConfig((C) allocateInstance(clazz), in);
    }

    public <C> C loadConfig(@NonNull C obj, @NonNull File file) throws IOException {
        return this.loadConfig(obj, DataIn.wrap(file));
    }

    public <C> C loadConfig(@NonNull C obj, @NonNull DataIn in) throws IOException   {
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
                throw new IllegalArgumentException(String.format("Couldn't find field in %s with name %s!", clazz.getCanonicalName(), name));
            }
            Object val;
            if (subElement instanceof Element.ContainerElement) {
                if (field.getType() != Type.OBJECT) {
                    throw new IllegalStateException(String.format("Exception for field %s: field is %s but loaded a container!", name, field.getType()));
                }
                Config.Implementation implementation = field.getAnnotation(Config.Implementation.class);
                Class subClazz = implementation == null ? field.getClassType() : implementation.value();
                val = allocateInstance(subClazz);
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
                            throw PConstants.p_exception(e);
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
}
