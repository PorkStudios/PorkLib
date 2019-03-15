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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.orientation.advanced.Axis;
import net.daporkchop.lib.gui.component.orientation.advanced.Calculator;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import static net.daporkchop.lib.math.primitive.PMath.floorI;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class DistCalculator<T extends Component> implements Calculator<T> {
    @NonNull
    protected final DistUnit unit;
    protected double val;
    protected Axis axis;
    protected String relative;

    @Override
    public int get(BoundingBox bb, Container parent, T component, int[] dims) {
        switch (this.unit) {
            case PX:
                return floorI(this.val);
            case MULT:
                if (this.axis == null) {
                    throw new IllegalStateException("Axis must be set!");
                } else {
                    //int i = this.axis.getFrom(bb);
                    //double d = i * this.val;
                    //i = floorI(d);
                    //return i;
                    return floorI(this.axis.getFrom(bb) * this.val);
                }
            case RELATIVE:
                if (this.relative == null) {
                    throw new IllegalStateException("Relative element name must be set!");
                } else if (this.axis == null) {
                    throw new IllegalStateException("Axis must be set!");
                }
                Component relative = parent.getChild(this.relative);
                if (relative == null) {
                    throw new IllegalStateException(String.format("Couldn't find element with name: \"%s\"", this.relative));
                } else {
                    return this.axis.getFrom(relative.getBounds());
                }
            case CM:
                throw new UnsupportedOperationException("cm");
        }
        throw new IllegalStateException();
    }
}