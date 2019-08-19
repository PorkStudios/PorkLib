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

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.ai.NeuralNetwork;
import net.daporkchop.lib.ai.alg.TrainingOptions;

/**
 * Options for training a neural network using the NEAT algorithm.
 *
 * @author DaPorkchop_
 */
@Accessors(chain = true, fluent = true)
@Getter
@Setter
public class NEATOptions extends TrainingOptions<NEATNetwork, NEATOptions> {
    protected double geneDisableChance = 0.75D;
    protected double mutationWeightChance = 0.8D;
    protected double mutationWeightRandomChance = 0.1D;
    protected double mutationWeightMaxDisturbance = 0.25D;
    protected double mutationNewConnectionChance = 0.05D;
    protected double mutationNewNodeChance = 0.03D;
    protected double distanceExcessWeight = 1.0D;
    protected double distanceDisjointWeight = 1.0D;
    protected double distanceWeightsWeight = 0.4D;
    protected double speciesCompatibilityDistance = 0.8D;
    protected double generationEliminationPercentage = 0.9D;
    protected double breedCrossChance = 0.75D;
    protected double mutationWeightChanceRandomRange = 5.0D;
}
