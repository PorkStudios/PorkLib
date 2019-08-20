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

package net.daporkchop.lib.ai.alg.pgen.evolution;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * A group of specimens with unique traits inherited over multiple generations.
 *
 * A species will split in half once the previous species' survivors of a single generation are considered too "different"
 * from one another to produce offspring.
 *
 * However, there is still a low chance that survivors will breed with a member of another distinct species, bringing
 * traits from a totally different gene pool into the species (assuming the offspring survives).
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class Species {
    protected final List<Population> populations;

    protected final int branchedOnGeneration;
    protected int generation;

    public Species()    {
        this.populations = new ArrayList<>();

        this.branchedOnGeneration = this.generation = 0;
    }

    public Species(@NonNull Species parent) {
        this.populations = new ArrayList<>(parent.populations);

        this.branchedOnGeneration = this.generation = parent.generation;
    }
}
