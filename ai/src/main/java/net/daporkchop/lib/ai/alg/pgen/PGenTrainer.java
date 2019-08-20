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
import lombok.experimental.Accessors;
import net.daporkchop.lib.ai.Evaluator;
import net.daporkchop.lib.ai.NeuralNetwork;
import net.daporkchop.lib.ai.Trainer;

/**
 * Implementation of {@link Trainer} for the PGen algorithm.
 *
 * @author DaPorkchop_
 * @see PGen
 */
@Accessors(chain = true, fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class PGenTrainer implements Trainer<NeuralNetwork, PGenOptions> {
    @NonNull
    @Getter
    protected final Evaluator<NeuralNetwork> evaluator;
    @NonNull
    @Getter
    protected final PGenOptions options;
    @NonNull
    @Getter
    protected final PGen algorithm;

    protected final

    @Override
    public PGenNetwork fittestSpecimen() {
        return null;
    }

    @Override
    public synchronized void trainToFitness(double fitness) {
    }
}
