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

import net.daporkchop.lib.gui.GuiSystem;
import net.daporkchop.lib.gui.Window;
import net.daporkchop.lib.gui.swing.component.SwingButton;
import net.daporkchop.lib.gui.util.Dimensions;

/**
 * @author DaPorkchop_
 */
public class GuiExample {
    public static void main(String... args) throws InterruptedException {
        Window window = GuiSystem.swing().newWindow(new Dimensions(0, 0, 512, 256));
        window.setTitle("Example GUI").show();
        Thread.sleep(1000L);
        SwingButton button = new SwingButton("button1")
                .setClickHandler(() -> System.out.println("Clicked!"))
                .setText("Example Button")
                .setTooltip("This is a button!")
                .setX(dim -> dim.getWidth() / 10 - 35)
                .setY(dim -> dim.getHeight() / 10 - 10)
                .setWidth(dim -> 70)
                .setHeight(dim -> 20);
        window.addComponent("button1", button);
    }
}
