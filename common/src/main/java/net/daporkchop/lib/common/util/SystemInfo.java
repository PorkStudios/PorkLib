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

package net.daporkchop.lib.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Contains random stuff related to the current system
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class SystemInfo {
    public static final OperatingSystem OS;
    public static final String OS_NAME;
    public static final String OS_VERSION;
    public static final Architecture ARCH;
    public static final String ARCH_NAME;

    static {
        {
            OperatingSystem current = OperatingSystem.OTHER;
            OS_NAME = System.getProperty("os.name");
            String name = OS_NAME.toLowerCase();
            if (name.contains("windows")) {
                current = OperatingSystem.WINDOWS;
            } else if (name.contains("linux")) {
                current = OperatingSystem.LINUX;
            } else if (name.contains("mpe/ix")
                    || name.contains("freebsd")
                    || name.contains("irix")
                    || name.contains("digital unix")
                    || name.contains("unix")) {
                current = OperatingSystem.UNIX;
            } else if (name.contains("mac os")) {
                current = OperatingSystem.OSX_IS_BAD;
            } else if (name.contains("sun os")
                    || name.contains("sunos")
                    || name.contains("solaris")) {
                current = OperatingSystem.SOLARIS;
            }
            OS = current;
        }
        OS_VERSION = System.getProperty("os.version");
        {
            Architecture current = Architecture.OTHER;
            ARCH_NAME = System.getProperty("os.arch");
            String name = ARCH_NAME.toLowerCase();
            if (name.equals("x86")
                    || name.contains("i386")
                    || name.contains("i486")
                    || name.contains("i586")
                    || name.contains("i686")) {
                current = Architecture.x86_32;
            } else if (name.contains("x86_64")
                    || name.contains("x64")
                    || name.contains("amd64")) {
                current = Architecture.x86_64;
            } else if (name.contains("powerpc")) {
                current = Architecture.POWERPC;
            }
            ARCH = current;
        }
    }

    public enum OperatingSystem {
        WINDOWS,
        LINUX,
        UNIX,
        OSX_IS_BAD,
        SOLARIS,
        OTHER
    }

    public enum Architecture {
        x86_64,
        x86_32,
        POWERPC,
        OTHER
    }
}
