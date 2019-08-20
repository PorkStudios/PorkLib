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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.ai.alg.TrainingOptions;

import java.util.function.DoubleUnaryOperator;

/**
 * Options for training a neural network using the PGen algorithm.
 *
 * @author DaPorkchop_
 */
@Accessors(chain = true, fluent = true)
@Getter
@Setter
public class PGenOptions extends TrainingOptions<PGenNetwork, PGenOptions> {
    protected int inputs = 0;
    protected int outputs = 0;

    //these default values are somewhat very arbitrary
    protected double mutationNewNodeChance = 0.1d;
    protected double mutationNewConnectionChance = 0.2d;
    protected double mutationDeleteConnectionChance = 0.1d;
    protected double mutationWeightMaxDeviation = 0.06d;
    protected double speciesCrossBreedChance = 0.1d;

    protected int generationSurvivors = 64;

    protected int maxSpeciesSize = 1024;
    protected int maxSpeciesCount = 64;

    protected int baseSpeciesSize = 512;

    @NonNull
    protected DoubleUnaryOperator activationFunction = x -> 1.0D / (1.0D + Math.exp(-4.9D * x)); //Mythan's "CustomizedSigmoidActivation"
}
