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

package net.daporkchop.lib.gui.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.ToIntFunction;

/**
 * Recalculates the dimensions of an element when the window is resized
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface DimensionCalculator {
    static DimensionCalculator defaultInstance()    {
        return new DefaultDimensionCalculator();
    }

    static DimensionCalculator of(@NonNull ToIntFunction<Dimensions> x, @NonNull ToIntFunction<Dimensions> y, @NonNull ToIntFunction<Dimensions> width, @NonNull ToIntFunction<Dimensions> height) {
        return new DefaultDimensionCalculator(x, y, width, height);
    }

    /**
     * Recalculates a component's dimensions
     *
     * @param windowDimensions the new size of the window
     * @return the component's new dimensions
     */
    Dimensions update(@NonNull Dimensions windowDimensions);

    default DimensionCalculator setX(@NonNull ToIntFunction<Dimensions> x)  {
        throw new UnsupportedOperationException();
    }

    default DimensionCalculator setY(@NonNull ToIntFunction<Dimensions> y)  {
        throw new UnsupportedOperationException();
    }

    default DimensionCalculator setWidth(@NonNull ToIntFunction<Dimensions> width)  {
        throw new UnsupportedOperationException();
    }

    default DimensionCalculator setHeight(@NonNull ToIntFunction<Dimensions> height)  {
        throw new UnsupportedOperationException();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Accessors(chain = true)
    class DefaultDimensionCalculator implements DimensionCalculator {
        @NonNull
        protected ToIntFunction<Dimensions> x;

        @NonNull
        protected ToIntFunction<Dimensions> y;

        @NonNull
        protected ToIntFunction<Dimensions> width;

        @NonNull
        protected ToIntFunction<Dimensions> height;

        @Override
        public Dimensions update(Dimensions windowDimensions) {
            return new Dimensions(
                    this.x.applyAsInt(windowDimensions),
                    this.y.applyAsInt(windowDimensions),
                    this.width.applyAsInt(windowDimensions),
                    this.height.applyAsInt(windowDimensions)
            );
        }
    }
}
