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

package net.daporkchop.lib.ai.alg.pgen;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.ai.NeuralNetwork;
import net.daporkchop.lib.ai.alg.abst.AbstractNeuralNetwork;

import java.util.function.DoubleUnaryOperator;

/**
 * An implementation of {@link NeuralNetwork} for use in PGen training.
 *
 * This is not a particularly efficient implementation, it needs a lot of optimization both in terms of memory and
 * CPU resources, and doesn't allow multithreaded access, but it's basically just a proof-of-concept.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true, chain = true)
public class PGenNetwork extends AbstractNeuralNetwork {
    protected final InputNode[] inputNodes;
    protected final OutputNode[] outputNodes;
    protected final HiddenNode[] hiddenNodes;
    protected final DoubleUnaryOperator activationFunction;

    public PGenNetwork(@NonNull InputNode[] inputNodes, @NonNull OutputNode[] outputNodes, @NonNull HiddenNode[] hiddenNodes, @NonNull DoubleUnaryOperator activationFunction) {
        super(inputNodes.length, outputNodes.length);

        this.inputNodes = inputNodes;
        this.outputNodes = outputNodes;
        this.hiddenNodes = hiddenNodes;
        this.activationFunction = activationFunction;
    }

    @Override
    public synchronized void compute(@NonNull double[] inputs, @NonNull double[] outputs) throws IllegalArgumentException {
        this.validateParameters(inputs, outputs);

        //copy values into input nodes
        for (int i = this.inputs - 1; i >= 0; i--)  {
            this.inputNodes[i].value = inputs[i];
        }

        //reset hidden nodes
        for (int i = this.hiddenNodes.length - 1; i >= 0; i--) {
            this.hiddenNodes[i].reset();
        }

        //compute output values
        for (int i = this.outputs - 1; i >= 0; i--) {
            outputs[i] = this.outputNodes[i].getValue(this.activationFunction);
        }
    }

    @Setter(AccessLevel.PACKAGE)
    protected double fitness = Double.NaN;

    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true, chain = true)
    public static final class Connection    {
        @NonNull
        private final Node from;
        @NonNull
        private final DependantNode to;

        private final double weight;

        private final int innovationNumber;
        private final boolean enabled;
    }

    @Getter
    @Accessors(fluent = true)
    public static abstract class Node {
        @Getter(AccessLevel.NONE)
        protected double value = Double.NaN;

        public double getValue(DoubleUnaryOperator activationFunction)  {
            return this.value;
        }

        public final void reset()   {
            this.value = Double.NaN;
        }
    }

    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true)
    public static abstract class DependantNode extends Node {
        @NonNull
        protected final Connection[] connections;

        public final double getValue(DoubleUnaryOperator activationFunction)    {
            double value = this.value;
            if (value == Double.NaN)  {
                value = 0.0d;

                for (int i = this.connections.length; i >= 0; i--)  {
                    Connection connection = this.connections[i];
                    if (connection.enabled) {
                        value += connection.from.getValue(activationFunction) * connection.weight;
                    }
                }

                this.value = value = activationFunction.applyAsDouble(value);
            }
            return value;
        }
    }

    @Getter
    @Accessors(fluent = true, chain = true)
    public static final class InputNode extends Node {
    }

    @Getter
    @Accessors(fluent = true, chain = true)
    public static final class HiddenNode extends DependantNode    {
        public HiddenNode(Connection[] connections) {
            super(connections);
        }
    }

    @Getter
    @Accessors(fluent = true, chain = true)
    public static final class OutputNode extends DependantNode    {
        public OutputNode(Connection[] connections) {
            super(connections);
        }
    }
}
