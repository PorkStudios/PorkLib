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
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.primitive.lambda.consumer.bi.IntegerObjectConsumer;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Registry<T extends RegistryEntry> {
    private final List<T> idToEntries = new ArrayList<>();

    @NonNull
    @Getter
    private final RegistryType type;

    public synchronized void registerEntry(@NonNull T value, int id) {
        if (this.idToEntries.get(id) != null)   {
            throw new IllegalArgumentException(String.format("ID %d is already occupied by %s", id, this.idToEntries.get(id).toString()));
        }
        this.idToEntries.set(id, value);
    }

    public void forEachEntry(@NonNull IntegerObjectConsumer<T> consumer)    {
        for (int i = 0; i < this.idToEntries.size(); i++)   {
            consumer.accept(i, this.idToEntries.get(i));
        }
    }

    public T getValue(int id)   {
        return this.idToEntries.get(id);
    }

    public int getId(@NonNull T value)  {
        return this.idToEntries.indexOf(value);
    }

    public int getId(@NonNull ResourceLocation name)  {
        for (int i = this.idToEntries.size() - 1; i >= 0; i--)  {
            if (name == this.idToEntries.get(i).getRegistryName() || name.equals(this.idToEntries.get(i).getRegistryName()))  {
                return i;
            }
        }

        return -1;
    }

    public boolean hasValue(T value)    {
        return this.idToEntries.contains(value);
    }
}
