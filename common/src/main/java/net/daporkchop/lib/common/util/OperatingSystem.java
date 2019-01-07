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

/**
 * Used for identifying the current operating system
 *
 * @author DaPorkchop_
 */
public enum OperatingSystem {
    WINDOWS,
    LINUX,
    UNIX,
    OSX_IS_BAD,
    SOLARIS,
    OTHER;

    public static final OperatingSystem CURRENT;
    public static final String NAME_CURRENT;

    static {
        OperatingSystem current = OTHER;
        NAME_CURRENT = System.getProperty("os.name");
        String name = NAME_CURRENT.toLowerCase();
        if (name.contains("windows")) {
            current = WINDOWS;
        } else if (name.contains("linux")) {
            current = LINUX;
        } else if (name.contains("mpe/ix")
                || name.contains("freebsd")
                || name.contains("irix")
                || name.contains("digital unix")
                || name.contains("unix"))   {
            current = UNIX;
        } else if (name.contains("mac os")) {
            current = OSX_IS_BAD;
        } else if (name.contains("sun os")
                || name.contains("sunos")
                || name.contains("solaris")) {
            current = SOLARIS;
        }
        CURRENT = current;
    }
}
