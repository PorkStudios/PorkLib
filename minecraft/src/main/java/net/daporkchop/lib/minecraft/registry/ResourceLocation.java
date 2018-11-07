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

package net.daporkchop.lib.minecraft.registry;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ResourceLocation {
    @NonNull
    private final String modid;

    @NonNull
    private final String name;

    public ResourceLocation(@NonNull String name) {
        String[] split = name.split(":");
        if (split.length != 2 || split[0].isEmpty() || split[1].isEmpty()/* || split[0].contains(" ") || split[1].contains(" ")*/) {
            throw new IllegalArgumentException(String.format("Invalid resource location: %s", name));
        }
        this.modid = split[0];
        this.name = split[1];
    }

    public ResourceLocation(@NonNull String modid, @NonNull String name) {
        if (modid.isEmpty()) {
            throw new IllegalArgumentException("modid may not be empty!");
        } else if (name.isEmpty()) {
            throw new IllegalArgumentException("name may not be empty!");
        } else if (modid.contains(":") || name.contains(":")) {
            throw new IllegalArgumentException(String.format("Neither modid nor name may contain a colon! (given: modid=%s, name=%s)", modid, name));
        }/* else if (modid.contains(" ") || name.contains(" ")) {
            throw new IllegalArgumentException(String.format("Neither modid nor name may contain a space! (given: modid=%s, name=%s)", modid, name));
        }*/
        this.modid = modid;
        this.name = name;
    }

    @Override
    public int hashCode() {
        return this.modid.hashCode() * 31 + this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ResourceLocation)) {
            return false;
        }
        ResourceLocation resourceLocation = (ResourceLocation) obj;
        return this.modid.equals(resourceLocation.modid) && this.name.equals(resourceLocation.name);
    }

    @Override
    public String toString() {
        return String.format("%s:%s", this.modid, this.name);
    }
}
