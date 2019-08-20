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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.ai.Evaluator;
import net.daporkchop.lib.ai.NeuralNetwork;
import net.daporkchop.lib.ai.Trainer;
import net.daporkchop.lib.ai.alg.pgen.PGen;
import net.daporkchop.lib.ai.alg.pgen.PGenNetwork;
import net.daporkchop.lib.ai.alg.pgen.PGenOptions;
import net.daporkchop.lib.ai.alg.pgen.evolution.Species;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of {@link Trainer} for the PGen algorithm.
 *
 * @author DaPorkchop_
 * @see PGen
 */
@Accessors(chain = true, fluent = true)
public class PGenTrainer implements Trainer<NeuralNetwork, PGenOptions> {
    @Getter
    protected final Evaluator<NeuralNetwork> evaluator;
    @Getter
    protected final PGenOptions options;

    protected final List<Species> species = new ArrayList<>();
    protected final int[] activeSpecies;
    protected int generation = 0;

    public PGenTrainer(@NonNull Evaluator<NeuralNetwork> evaluator, @NonNull PGenOptions options)   {
        this.evaluator = evaluator;
        this.options = options;

        Arrays.fill(this.activeSpecies = new int[options.maxSpeciesCount()], -1);
    }

    @Override
    public PGenNetwork fittestSpecimen() {
        return null;
    }

    @Override
    public synchronized void trainToFitness(double fitness) {
        while (this.fittestSpecimen() == null || this.fittestSpecimen().fitness() < fitness)    {
        }
    }

    protected void buildNextGeneration()    {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (this.generation > 0)    {
            //build generation based on previous one
        } else {
            //training has not started yet, generate random base population
            Species species = new Species(this);
            Population population = new Population(new Specimen[this.options.baseSpeciesSize()], species);
        }
    }
}
