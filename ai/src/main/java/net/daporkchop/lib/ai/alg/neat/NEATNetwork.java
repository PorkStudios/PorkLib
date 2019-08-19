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

package net.daporkchop.lib.ai.alg.neat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.ai.NeuralNetwork;
import net.daporkchop.lib.ai.alg.abst.AbstractNeuralNetwork;

/**
 * An implementation of {@link NeuralNetwork} for use in NEAT training.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true, chain = true)
@Getter
public class NEATNetwork extends AbstractNeuralNetwork {


    @Override
    public void compute(@NonNull double[] inputs, @NonNull double[] outputs) throws IllegalArgumentException {
    }

    @Setter(AccessLevel.PACKAGE)
    protected double fitness = Double.NaN;

    @Accessors(fluent = true)
    @Getter
    public static abstract class BaseNode   {
        protected double weight;
        protected int innovationNumber; //not quite sure what this is yet...
        protected boolean enabled;
    }

    public static abstract class DependantNode extends BaseNode {

    }

    public static final class InputNode extends BaseNode    {
        protected int inputIndex;
    }

    public static final class HiddenNode extends DependantNode    {
        protected int cacheIndex;
    }

    public static final class OutputNode extends DependantNode    {
        protected int outputIndex;
    }
}
