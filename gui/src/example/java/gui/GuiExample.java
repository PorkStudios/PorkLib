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

package gui;

import net.daporkchop.lib.graphics.bitmap.image.PImage;
import net.daporkchop.lib.gui.GuiSystem;
import net.daporkchop.lib.gui.component.orientation.advanced.Axis;
import net.daporkchop.lib.gui.component.orientation.advanced.calculator.DistUnit;
import net.daporkchop.lib.gui.util.Alignment;

/**
 * @author DaPorkchop_
 */
public class GuiExample {
    public static void main(String... args) {
        GuiSystem.swing().newWindow(64, 64, 512, 256)
                .setTitle("Example GUI").setIcon(PImage.randomImage(32, 32), PImage.randomImage(16, 16))
                .button("button1", button -> button.setOrientation(0.3d, 0.15d, 0.4d, 0.1d)
                        .setText("Example Button!")
                        .setTooltip("This is a tooltip that will be shown when hovering the mouse over the button.")
                        .setTextPos(Alignment.TOP_LEFT)
                        .setIcon(PImage.randomImage(32, 32)))
                .button("button2", button -> button.setOrientation(0, 0.0d, 0.1d, 0.1d)
                        .setClickHandler((mouseButton, x, y) -> System.out.printf("Bounds: %s\n", button.getWindow().getComponent("panel1.button2").getBounds())))
                .label("label1", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", label -> label
                        .setOrientation(0.5d, 0, 0.5d, 0.1d)
                        .setTooltip("This is a label. Labels can only display plain text."))
                .button("button3", button -> button.orientAdvanced(orientation -> orientation
                        .configureAxis(Axis.X, calc -> calc.ease(DistUnit.PX, 0))
                        .configureAxis(Axis.Y, calc -> calc.ease(DistUnit.MULT, 0.5, Axis.HEIGHT)
                                .min(DistUnit.PX, 50, DistUnit.MULT, 0.5, Axis.WIDTH))
                        .configureAxis(Axis.WIDTH, calc -> calc.ease(DistUnit.PX, 30))
                        .configureAxis(Axis.HEIGHT, Axis.WIDTH)))
                .show();
    }
}
