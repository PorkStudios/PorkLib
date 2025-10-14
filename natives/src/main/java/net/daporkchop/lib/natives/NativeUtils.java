/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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
 */

package net.daporkchop.lib.natives;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.daporkchop.lib.common.system.PlatformInfo;

import java.lang.management.ManagementFactory;
import java.util.Arrays;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class NativeUtils {
    private static boolean isGC(String name) {
        val garbageCollectors = ManagementFactory.getGarbageCollectorMXBeans();
        if (garbageCollectors.isEmpty()) {
            return false;
        }

        for (val garbageCollector : garbageCollectors) {
            if (!Arrays.asList(garbageCollector.getName().split(" ")).contains(name)) {
                return false;
            }
        }

        return true;
    }

    public static boolean allowJniCritical() {
        //if system property is set, use that
        String allowCritical = System.getProperty("porklib.natives.allowJniCritical");
        if (allowCritical != null) {
            return Boolean.parseBoolean(allowCritical);
        }

        return (PlatformInfo.JAVA_VERSION >= 15 && isGC("Shenandoah"))
                //see JEP 423: https://bugs.openjdk.org/browse/JDK-8276094
                || (PlatformInfo.JAVA_VERSION >= 22 && isGC("G1"));
    }
}
