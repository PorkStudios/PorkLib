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

package net.daporkchop.lib.primitive.generator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayDeque;
import java.util.Collection;

@Accessors(chain = true)
@Setter
@Getter
@NoArgsConstructor
public class Primitive {
    public static final Collection<Primitive> primitives = new ArrayDeque<>();
    public static final String PARAM_DEF = "P%d";
    public static final String FULLNAME_DEF = String.format("_%s_", PARAM_DEF);
    public static final String FULLNAME_FORCE_DEF = String.format("_fullname%s_", PARAM_DEF);
    public static final String NAME_DEF = String.format("_%s_", PARAM_DEF.toLowerCase());
    public static final String NAME_FORCE_DEF = String.format("_name%s_", PARAM_DEF);
    public static final String HASHCODE_DEF = String.format("_hashCode%s_", PARAM_DEF);
    public static final String EQUALS_DEF = String.format("_equals%s_", PARAM_DEF);
    public static final String CAST_DEF = String.format("_cast%s_", PARAM_DEF);
    public static final String EMPTYVALUE_DEF = String.format("_%sE_", PARAM_DEF);
    public static final String NON_GENERIC_DEF = String.format("_nG%s_", PARAM_DEF);
    public static final String GENERIC_DEF = String.format("_G%s_", PARAM_DEF);
    public static final String GENERIC_SUPER_P_DEF = String.format("_Gsuper%s_", PARAM_DEF);
    public static final String GENERIC_EXTENDS_P_DEF = String.format("_Gextends%s_", PARAM_DEF);

    public static final String GENERIC_HEADER_DEF = "_gH_";
    public static final String GENERIC_SUPER_DEF = "_gSuper_";
    public static final String GENERIC_EXTENDS_DEF = "_gExtends_";

    public static final String HEADERS_DEF = "_headers_";
    public static final String LICENSE_DEF = "_copyright_";
    public static final String PACKAGE_DEF = "_package_";
    public static final String IMPORTS_DEF = "_imports_";

    public static final String METHODS_DEF = "_methods_";

    @NonNull
    private String fullName;
    @NonNull
    private String name;
    @NonNull
    private String hashCode;
    @NonNull
    private boolean generic;
    @NonNull
    private String emptyValue;
    @NonNull
    private String equals;

    public String format(@NonNull String text, int i) {
        return this.format(text, i, true);
    }

    public String format(@NonNull String text, int i, boolean removeGenericThings) {
        if (i == 0) {
            if (this.generic) {
                if (removeGenericThings){
                    text = text
                            .replaceAll("<%", "")
                            .replaceAll("%>", "");
                }
            } else {
                text = text.replaceAll("<%([\\s\\S]*?)%>", "");
            }
        }
        return text
                .replaceAll(String.format(FULLNAME_DEF, i), this.fullName)
                .replaceAll(String.format(FULLNAME_FORCE_DEF, i), this.generic ? String.valueOf((char) ('A' + i)) : this.fullName)
                .replaceAll(String.format(NAME_DEF, i), this.generic ? String.valueOf((char) ('A' + i)) : this.name)
                .replaceAll(String.format(NAME_FORCE_DEF, i), this.name)
                .replaceAll(String.format(HASHCODE_DEF, i), this.hashCode)
                .replaceAll(String.format(EQUALS_DEF, i), this.equals)
                .replaceAll(String.format(CAST_DEF, i), this.generic ? "(" + (char) ('A' + i) + ") " : "")
                .replaceAll(String.format(EMPTYVALUE_DEF, i), this.emptyValue)
                .replaceAll(String.format(NON_GENERIC_DEF, i), this.generic ? "" : this.name)
                .replaceAll(String.format(GENERIC_DEF, i), this.generic ? "<" + ((char) ('A' + i)) + "> " : "")
                .replaceAll(String.format(GENERIC_SUPER_P_DEF, i), getGenericSuper(i, this))
                .replaceAll(String.format(GENERIC_EXTENDS_P_DEF, i), getGenericExtends(i, this));
    }

    public Primitive setGeneric() {
        this.generic = true;
        return this;
    }

    @Override
    public String toString() {
        return this.fullName;
    }

    public static int countVariables(@NonNull String filename) {
        for (int i = 0; ; i++) {
            String s = String.format(FULLNAME_DEF, i);
            if (!filename.contains(s)) {
                return i;
            }
        }
    }

    public static String getGenericHeader(Primitive... primitives) {
        if (primitives.length == 0) {
            return "";
        }
        int i = 0;
        for (Primitive p : primitives) {
            if (p.generic) {
                i++;
            }
        }
        if (i == 0) {
            return "";
        }
        String s = "<";
        for (int j = 0; j < primitives.length; j++) {
            if (primitives[j].generic) {
                s += (char) ('A' + j);
                s += ", ";
            }
        }
        return (s.endsWith(", ") ? s.substring(0, s.length() - 2) : s) + '>';
    }

    public static String getGenericSuper(Primitive... primitives) {
        if (primitives.length == 0) {
            return "";
        }
        int i = 0;
        for (Primitive p : primitives) {
            if (p.generic) {
                i++;
            }
        }
        if (i == 0) {
            return "";
        }
        String s = "<";
        for (int j = 0; j < primitives.length; j++) {
            if (primitives[j].generic) {
                s += "? super ";
                s += (char) ('A' + j);
                s += ", ";
            }
        }
        if (s.endsWith(", ")) {
            s = s.substring(0, s.length() - 2);
        }
        return s + '>';
    }

    public static String getGenericExtends(Primitive... primitives) {
        if (primitives.length == 0) {
            return "";
        }
        int i = 0;
        for (Primitive p : primitives) {
            if (p.generic) {
                i++;
            }
        }
        if (i == 0) {
            return "";
        }
        String s = "<";
        for (int j = 0; j < primitives.length; j++) {
            if (primitives[j].generic) {
                s += "? extends ";
                s += (char) ('A' + j);
                s += ", ";
            }
        }
        if (s.endsWith(", ")) {
            s = s.substring(0, s.length() - 2);
        }
        return s + '>';
    }

    public static String getGenericSuper(int x, Primitive... primitives) {
        if (primitives.length == 0) {
            return "";
        }
        int i = 0;
        for (Primitive p : primitives) {
            if (p.generic) {
                i++;
            }
        }
        if (i == 0) {
            return "";
        }
        String s = "<";
        for (int j = 0; j < primitives.length; j++) {
            if (primitives[j].generic) {
                s += "? super ";
                s += (char) ('A' + j + x);
                s += ", ";
            }
        }
        if (s.endsWith(", ")) {
            s = s.substring(0, s.length() - 2);
        }
        return s + '>';
    }

    public static String getGenericExtends(int x, Primitive... primitives) {
        if (primitives.length == 0) {
            return "";
        }
        int i = 0;
        for (Primitive p : primitives) {
            if (p.generic) {
                i++;
            }
        }
        if (i == 0) {
            return "";
        }
        String s = "<";
        for (int j = 0; j < primitives.length; j++) {
            if (primitives[j].generic) {
                s += "? extends ";
                s += (char) ('A' + j + x);
                s += ", ";
            }
        }
        if (s.endsWith(", ")) {
            s = s.substring(0, s.length() - 2);
        }
        return s + '>';
    }
}
