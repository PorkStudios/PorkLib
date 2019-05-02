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

package net.daporkchop.lib.gui.component.orientation.advanced.calculator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.orientation.advanced.Calculator;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author DaPorkchop_
 */
@Getter
public class SumCalculator<T extends Component> implements Calculator<T> {
    protected final Collection<Calculator<T>> degligates = new HashSet<>();

    @Override
    public int get(BoundingBox bb, Container parent, T component, int[] dims) {
        int i = 0;
        for (Calculator<T> calculator : this.degligates)    {
            i += calculator.get(bb, parent, component, dims);
        }
        return i;
    }

    @Override
    public SumCalculator<T> plus(@NonNull Calculator<T> other) {
        this.degligates.add(other);
        return this;
    }

    public Calculator<T> build() {
        if (this.degligates.isEmpty())  {
            return NullCalculator.getInstance();
        } else if (this.degligates.size() == 1)  {
            return this.degligates.iterator().next();
        } else {
            return this;
        }
    }
}
