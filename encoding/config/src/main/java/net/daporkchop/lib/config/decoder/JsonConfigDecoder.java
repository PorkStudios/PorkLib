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

package net.daporkchop.lib.config.decoder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.LinkedTreeMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.config.attribute.Comment;
import net.daporkchop.lib.config.util.Element;
import net.daporkchop.lib.reflection.PField;
import net.daporkchop.lib.reflection.util.Type;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
public class JsonConfigDecoder implements ConfigDecoder {
    protected final JsonParser parser = new JsonParser();
    //protected final PField<LinkedTreeMap<String, JsonElement>> jsonObject_members = PField.of(JsonObject.class, "members");
    protected final PField<Number> jsonPrimitive_value = PField.of(JsonPrimitive.class, "value");

    @Override
    public Element.ContainerElement decode(@NonNull DataIn in) throws IOException {
        JsonElement element;
        try (Reader reader = new InputStreamReader(in)) {
            element = this.parser.parse(reader);
        }
        if (!element.isJsonObject()) {
            throw new IllegalStateException("Not a JSON object!");
        }
        PJsonObject root = new PJsonObject(Type.OBJECT, "", null);
        this.loadInto(root, element.getAsJsonObject());
        return root;
    }

    @SuppressWarnings("unchecked")
    protected void loadInto(@NonNull PJsonObject element, @NonNull JsonObject object) {
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            Element subElement;
            JsonElement json = entry.getValue();
            if (json.isJsonObject()) {
                subElement = new PJsonObject(Type.OBJECT, entry.getKey(), element);
                this.loadInto((PJsonObject) subElement, json.getAsJsonObject());
            } else if (json.isJsonArray()) {
                //TODO
                throw new UnsupportedOperationException();
            } else if (json.isJsonNull()) {
                throw new UnsupportedOperationException("Null values!");
            } else {
                //get the freaking value
                JsonPrimitive primitive = json.getAsJsonPrimitive();
                if (primitive.isString())   {
                    subElement = new PJsonElement(Type.STRING, primitive.getAsString(), entry.getKey(), element);
                } else if (primitive.isNumber()) {
                    Number num = this.jsonPrimitive_value.get(primitive);
                    if (num instanceof LazilyParsedNumber) {
                        LazilyParsedNumber lazy = (LazilyParsedNumber) num;
                        String s = lazy.toString();
                        try { //no i'm not sorry about this
                            num = Integer.parseInt(s);
                        } catch (NumberFormatException e1) {
                            try {
                                num = Long.parseLong(s);
                            } catch (NumberFormatException e2) {
                                try {
                                    num = new BigInteger(s);
                                } catch (NumberFormatException e3) {
                                    try {
                                        num = Double.parseDouble(s);
                                    } catch (NumberFormatException e4) {
                                        try {
                                            num = new BigDecimal(s);
                                        } catch (NumberFormatException e5) {
                                            throw new IllegalArgumentException(String.format("Invalid value: %s", s));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Type type = Type.getMoreAccurateType(num.getClass(), true, false);
                    subElement = new PJsonElement(type, num, entry.getKey(), element);
                } else if (primitive.isBoolean())   {
                    subElement = new PJsonElement(Type.BOOLEAN, primitive.getAsBoolean(), entry.getKey(), element);
                } else {
                    throw new UnsupportedOperationException(String.format("Couldn't parse value: %s", primitive));
                }
            }
            element.setElement(entry.getKey(), subElement);
        }
    }

    @AllArgsConstructor
    @Getter
    @Setter
    protected static class PJsonElement<V> implements Element<V> {
        @NonNull
        protected Type type;

        @NonNull
        protected V value;

        @NonNull
        protected final String name;

        protected final PJsonElement parent;

        @Override
        public Comment getComment() {
            return null;
        }

        @Override
        public void setComment(Comment comment) {
        }
    }

    protected static class PJsonObject extends PJsonElement<Map<String, Element>> implements Element.ContainerElement {
        public PJsonObject(Type type, String name, PJsonElement parent) {
            super(type, new LinkedTreeMap<>(), name, parent);
        }
    }
}
