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
import net.daporkchop.lib.ai.alg.abst.AbstractNeuralNetwork;

import java.util.LinkedList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * A single member of a species.
 * <p>
 * This is a rather inefficient implementation by itself, and is really only intended to be used directly by the
 * trainer for evaluation purposes. A specialized implementation without all the trait information is provided to
 * the evaluator to speed up execution.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public class Specimen extends AbstractNeuralNetwork {
    protected static final int INPUT_MASK = 0x80000000;
    protected static final int OUTPUT_MASK = 0x40000000;
    protected static final int NONE_MASK = ~(INPUT_MASK | OUTPUT_MASK);

    @Getter
    protected final Species species;
    @Getter
    protected final Population population;
    protected final DoubleUnaryOperator activationFunction;

    protected final Output

    public Specimen(@NonNull Population population) {
        super(population.species.trainer.options().inputs(), population.species.trainer.options().outputs());

        this.species = population.species;
        this.population = population;
        this.activationFunction = this.species.trainer.options().activationFunction();
    }

    @Override
    public void compute(@NonNull double[] inputs, @NonNull double[] outputs) throws IllegalArgumentException {
    }

    protected interface Node {
        int id();

        double value(Specimen specimen, double[] inputs);
    }

    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true)
    protected static final class Input implements Node {
        protected final int sourceIndex;

        @Override
        public int id() {
            return this.sourceIndex | INPUT_MASK;
        }

        @Override
        public double value(Specimen specimen, double[] inputs) {
            return inputs[this.sourceIndex];
        }
    }

    @Getter
    @Accessors(fluent = true)
    protected static abstract class ConsumerNode implements Node {
        protected final List<Node> sources = new LinkedList<>();

        @Override
        public double value(Specimen specimen, double[] inputs) {
            double val = 0.0d;
            for (Node node : this.sources) {
                val += node.value(specimen, inputs);
            }
            return specimen.activationFunction.applyAsDouble(val);
        }
    }

    @Getter
    @Accessors(fluent = true)
    protected static final class HiddenNode extends ConsumerNode {
        protected double weight;
        protected final List<Node> sources = new LinkedList<>();
        protected Node dst;
        protected int id;

        @Override
        public double value(Specimen specimen, double[] inputs) {
            return super.value(specimen, inputs) * this.weight;
        }
    }

    protected static final class OutputNode extends ConsumerNode    {

    }
}
