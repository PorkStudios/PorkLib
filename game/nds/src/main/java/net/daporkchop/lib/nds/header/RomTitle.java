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

package net.daporkchop.lib.nds.header;

import lombok.Getter;
import lombok.NonNull;

import java.nio.charset.Charset;

/**
 * @author DaPorkchop_
 */
@Getter
public class RomTitle {
    protected final String title;
    protected final String subtitle;
    protected final String manufacturer;

    public RomTitle(@NonNull byte[] arr)    {
        String textFull = new String(arr, Charset.forName("UTF-16LE"));
        String[] split = textFull.trim().split("\\u000A");
        if (split.length == 2)  {
            this.title = split[0];
            this.subtitle = "";
            this.manufacturer = split[1];
        } else if (split.length == 3)   {
            this.title = split[0];
            this.subtitle = split[1];
            this.manufacturer = split[2];
        } else {
            throw new IllegalArgumentException(String.format("Couldn't parse title string: \"%s\"", textFull));
        }
    }
}
