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

package net.daporkchop.lib.ai;

import lombok.NonNull;

/**
 * An {@link AI} based on a network of interconnected nodes.
 * <p>
 * A neural network typically has a fixed number of inputs and outputs.
 *
 * @author DaPorkchop_
 */
public interface NeuralNetwork extends AI {
    /**
     * Runs the network on the given input data, storing the output values in the given array.
     *
     * @param inputs  an array containing the input values
     * @param outputs an array that the output values will be stored in
     * @throws IllegalArgumentException if either parameters are not exactly the correct size (see {@link #inputs()} and {@link #outputs()}, respectively)
     */
    void compute(@NonNull double[] inputs, @NonNull double[] outputs) throws IllegalArgumentException;

    /**
     * Runs the network on the given input data, storing the output values in the returned array.
     * <p>
     * This method should be avoided in favor of {@link #compute(double[], double[])} if possible.
     *
     * @param inputs an array containing the input values
     * @return an array containing the output values
     * @throws IllegalArgumentException if the input array is not exactly the same size as {@link #inputs()}
     */
    default double[] compute(@NonNull double[] inputs) throws IllegalArgumentException {
        double[] outputs = new double[this.outputs()];
        this.compute(inputs, outputs);
        return outputs;
    }

    /**
     * @return the network's input count
     */
    int inputs();

    /**
     * @return the network's output count
     */
    int outputs();
}
