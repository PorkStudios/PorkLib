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

package net.daporkchop.lib.common.system;

import lombok.experimental.UtilityClass;

import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Contains information about the current platform, such as CPU architecture, operating system and Java version.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PlatformInfo {
    public final Architecture ARCHITECTURE;

    static {
        //obligatory "i use arch btw" comment
        String arch = System.getProperty("os.arch", "").toLowerCase().replaceAll("[^a-z0-9]+", "");

        switch (arch) {
            case "x8664":
            case "amd64":
            case "ia32e":
            case "em64t":
            case "x64":
                ARCHITECTURE = Architecture.x86_64;
                break;
            case "x8632":
            case "x86":
            case "i386":
            case "i486":
            case "i586":
            case "i686":
            case "ia32":
            case "x32":
                ARCHITECTURE = Architecture.x86;
                break;
            case "ia64":
            case "itanium64":
                ARCHITECTURE = Architecture.Itanium;
                break;
            case "sparc":
            case "sparc32":
                ARCHITECTURE = Architecture.SPARC;
                break;
            case "sparcv9":
            case "sparc64":
                ARCHITECTURE = Architecture.SPARC_64;
                break;
            case "arm":
            case "arm32":
                ARCHITECTURE = Architecture.ARM;
                break;
            case "aarch64":
                ARCHITECTURE = Architecture.AARCH64;
                break;
            case "ppc":
            case "ppc32":
                ARCHITECTURE = Architecture.PowerPC;
                break;
            case "ppc64":
                ARCHITECTURE = Architecture.PowerPC_64;
                break;
            default:
                ARCHITECTURE = Architecture.UNKNOWN;
        }
    }

    public final OperatingSystem OPERATING_SYSTEM;

    static {
        String os = System.getProperty("os.name", "").toLowerCase().replaceAll("[^a-z0-9]+", "");

        if (os.startsWith("linux")) {
            OPERATING_SYSTEM = OperatingSystem.Linux;
        } else if (os.startsWith("freebsd"))    {
            OPERATING_SYSTEM = OperatingSystem.FreeBSD;
        } else if (os.startsWith("openbsd"))    {
            OPERATING_SYSTEM = OperatingSystem.OpenBSD;
        } else if (os.startsWith("netbsd")) {
            OPERATING_SYSTEM = OperatingSystem.NetBSD;
        } else if (os.startsWith("solaris"))    {
            OPERATING_SYSTEM = OperatingSystem.Solaris;
        } else if (os.startsWith("windows"))    {
            OPERATING_SYSTEM = OperatingSystem.Windows;
        } else {
            OPERATING_SYSTEM = OperatingSystem.UNKNOWN;
        }
    }

    public final int JAVA_VERSION;

    static {
        int[] version = Arrays.stream(System.getProperty("java.specification.version", "1.6").split("\\.")).mapToInt(Integer::parseInt).toArray();

        JAVA_VERSION = version[0] == 1 ? version[1] : version[0];
    }

    public final boolean IS_32BIT = ARCHITECTURE.bits() == 32;
    public final boolean IS_64BIT = ARCHITECTURE.bits() == 64;

    public final boolean UNALIGNED;

    static {
        boolean unaligned = ARCHITECTURE == Architecture.x86 || ARCHITECTURE == Architecture.x86_64;
        if (!unaligned) {
            try {
                //TODO
            } finally {
            }
        }
        UNALIGNED = unaligned;
    }

    public final ByteOrder BYTE_ORDER = ByteOrder.nativeOrder();
    public final boolean IS_LITTLE_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
    public final boolean IS_BIG_ENDIAN    = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
}
