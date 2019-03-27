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

import lombok.NonNull;
import net.daporkchop.lib.graphics.bitmap.icon.PIcon;
import net.daporkchop.lib.graphics.bitmap.image.PImage;
import net.daporkchop.lib.graphics.bitmap.image.direct.DirectImageRGB;
import net.daporkchop.lib.gui.GuiEngine;
import net.daporkchop.lib.gui.component.orientation.advanced.Axis;
import net.daporkchop.lib.gui.component.orientation.advanced.calculator.DistUnit;
import net.daporkchop.lib.gui.component.state.functional.ButtonState;
import net.daporkchop.lib.gui.component.state.functional.CheckBoxState;
import net.daporkchop.lib.gui.component.state.functional.LabelState;
import net.daporkchop.lib.gui.component.type.Window;
import net.daporkchop.lib.gui.component.type.functional.ProgressBar;
import net.daporkchop.lib.gui.component.type.functional.Spinner;
import net.daporkchop.lib.gui.util.Alignment;
import net.daporkchop.lib.gui.util.ScrollCondition;
import net.daporkchop.lib.logging.Logging;

/**
 * @author DaPorkchop_
 */
public class GuiExample implements Logging {
    public static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
    public static final GuiEngine ENGINE = GuiEngine.swing();

    public static void main(String... args) {
        ENGINE.newWindow(64, 64, 512, 256)
                .setTitle("Example GUI").setIcon(PImage.randomImage(32, 32), PImage.randomImage(16, 16))
                .button("button1", button -> button
                        .orientRelative(0.3d, 0.15d, 0.4d, 0.1d)
                        .setText("Example Button!")
                        .setTooltip("This is a tooltip that will be shown when hovering the mouse over the button.")
                        .setTextPos(Alignment.TOP_LEFT)
                        .setTextColor(0xFF00AAAA)
                        .setIcon(filledImage(0xFF0000))
                        .setIcon(ButtonState.ENABLED_HOVERED, filledImage(0x0000FF))
                        .setIcon(ButtonState.ENABLED_CLICKED, filledImage(0x00FF00))
                        .addStateListener(state -> logger.debug("%s changed state: ${0}\n", button.getName(), state.name()))
                        .setClickHandler((mouseButton, x, y) -> button.getWindow().getComponent("label2").toggle())
                        .minDimensionsAreValueSize())
                .button("button2", button -> button
                        .orientRelative(0, 0.0d, 0.1d, 0.1d)
                        .setClickHandler((mouseButton, x, y) -> logger.info("Bounds: ${0}\n", button.getWindow().getComponent("panel1.button2").getBounds())))
                .label("label1", LOREM_IPSUM, label -> label
                        .orientRelative(0.5d, 0, 0.5d, 0.1d)
                        .setTooltip("This is a label. Labels can only display plain text.")
                        .setTextColor(0xFFFF00FF))
                .label("label2", label -> label
                        .orientRelative(0.8d, 0.7d, 0.1d, 0.1d)
                        .setIcon(filledImage(0x00FF00))
                        .addEnableListener(() -> logger.info("${0} was enabled!\n", label.getName()))
                        .setIcon(LabelState.DISABLED, filledImage(0xFF0000)))
                .button("button3", button -> button
                        .orientAdvanced(orientation -> orientation
                                .configureAxis(Axis.X, calc -> calc.ease(DistUnit.PX, 0))
                                .configureAxis(Axis.Y, calc -> calc.ease(DistUnit.MULT, 0.5, Axis.HEIGHT)
                                        .min(DistUnit.PX, 50, DistUnit.MULT, 0.5, Axis.WIDTH))
                                .configureAxis(Axis.WIDTH, calc -> calc.ease(DistUnit.PX, 30))
                                .configureAxis(Axis.HEIGHT, Axis.WIDTH)))
                .scrollPane("scrollPane1", scrollPane -> scrollPane
                        .orientRelative(0.2d, 0.6d, 0.6d, 0.4d)
                        .setScrolling(ScrollCondition.ALWAYS)
                        .label("label1", LOREM_IPSUM, label -> label
                                .orientRelative(0.0d, 0.0d, 0.75d, 0.15d)
                                .minDimensionsAreValueSize())
                        .label("label2", label -> label
                                .orientRelative(0.0d, 0.75d, 0.5d, 0.75d)
                                .setColor(0xFF5555FF)
                                .setText("Hello World!")))
                .button("button4", button -> button
                        .orientRelative(0.8d, 0.8d, 0.2d, 0.1d)
                        .setText("Dropdown test")
                        .setClickHandler((mouseButton, x, y) -> displayDropdownTestWindow(button.getWindow())))
                .button("button5", button -> button
                        .orientRelative(0.8d, 0.9d, 0.2d, 0.1d)
                        .setText("Scrollbar test")
                        .setClickHandler((mouseButton, x, y) -> displayScrollbarTestWindow(button.getWindow())))
                .addStateListener(state -> logger.debug("Window changed state: ${0}\n", state))
                .addVisibleListener(() -> logger.info("Window is now visible!"))
                .show();
    }

    public static void displayDropdownTestWindow(@NonNull Window parentWindow) {
        parentWindow.popup(128, 128, 512, 300)
                .setTitle("Dropdown menu test")
                .dropdown("dropdown1", ExampleEnum.class, dropdown -> dropdown
                        .orientRelative(0.05d, 0.05d, 0.2d, 0.1d)
                        .minDimensionsAreValueSize()
                        .addValueSelectedListener(value -> logger.info("Selected value changed to \"${0}\"!", value.name())))
                .checkBox("checkBox1", checkBox -> checkBox
                        .orientAdvanced(adv -> adv
                                .configureAxis(Axis.X, calc -> calc.ease(DistUnit.MULT, 0.05d, Axis.WIDTH))
                                .configureAxis(Axis.Y, calc -> calc.min(DistUnit.RELATIVE, "dropdown1", Axis.BELOW)))
                        .minDimensionsAreValueSize()
                        .setText("Dummy text box 1")
                        .setIcon(CheckBoxState.ENABLED_HOVERED_SELECTED, PImage.randomImage(32, 32))
                        .addSelectionListener(selected -> logger.info("Checkbox ${0}selected!", selected ? "" : "de")))
                .checkBox("checkBox2", checkBox -> checkBox
                        .orientAdvanced(adv -> adv
                                .configureAxis(Axis.X, calc -> calc.ease(DistUnit.MULT, 0.05d, Axis.WIDTH))
                                .configureAxis(Axis.Y, calc -> calc.min(DistUnit.RELATIVE, "checkBox1", Axis.BELOW)))
                        .minDimensionsAreValueSize()
                        .setText("Text box 2!")
                        .setIcon(PImage.randomImage(32, 32)))
                .radioGroupFast("group1")
                .radioButton("radioButton1", "group1", button -> button
                        .orientAdvanced(adv -> adv
                                .configureAxis(Axis.X, calc -> calc.min(DistUnit.RELATIVE, "dropdown1", Axis.RIGHT)
                                        .min(DistUnit.RELATIVE, "checkBox1", Axis.RIGHT)
                                        .min(DistUnit.RELATIVE, "checkBox2", Axis.RIGHT))
                                .configureAxis(Axis.Y, calc -> calc.ease(DistUnit.MULT, 0.05d, Axis.HEIGHT)))
                        .minDimensionsAreValueSize()
                        .setText("Radio button 1")
                        .addSelectionListener(selected -> logger.info("Radio button 1 ${0}selected!", selected ? "" : "de")))
                .radioButton("radioButton2", "group1", button -> button
                        .orientAdvanced(adv -> adv
                                .configureAxis(Axis.X, calc -> calc.min(DistUnit.RELATIVE, "radioButton1", Axis.X))
                                .configureAxis(Axis.Y, calc -> calc.min(DistUnit.RELATIVE, "radioButton1", Axis.BELOW)))
                        .minDimensionsAreValueSize()
                        .setText("Radio button 2"))
                .radioButton("radioButton3", "group1", button -> button
                        .orientAdvanced(adv -> adv
                                .configureAxis(Axis.X, calc -> calc.min(DistUnit.RELATIVE, "radioButton1", Axis.X))
                                .configureAxis(Axis.Y, calc -> calc.min(DistUnit.RELATIVE, "radioButton2", Axis.BELOW)))
                        .minDimensionsAreValueSize()
                        .setTextColor(0xFFFF0000)
                        .setText("Radio button 3"))
                .spinner("spinner1", spinner -> spinner
                        .orientAdvanced(adv -> adv
                                .configureAxis(Axis.X, calc -> calc.min(DistUnit.RELATIVE, "radioButton1", Axis.X))
                                .configureAxis(Axis.Y, calc -> calc.min(DistUnit.RELATIVE, "radioButton3", Axis.BELOW)))
                        .minDimensionsAreValueSize()
                        .setMinValue(-1000)
                        .setMaxValue(320)
                        .setStep(5)
                        .addChangeListener(value -> logger.info("Spinner value changed to ${0}!", value)))
                .show();
    }

    public static void displayScrollbarTestWindow(@NonNull Window parentWindow) {
        parentWindow.popup(128, 128, 512, 300)
                .setTitle("Scrollbar test")
                .spinner("max", 100, 1, Integer.MAX_VALUE, 1, spinner -> spinner
                        .orientAdvanced(adv -> adv
                                .x(0.05d)
                                .y(0.05d))
                        .minDimensionsAreValueSize()
                        .setPadding(2)
                        .addChangeListener(val -> {
                            spinner.getWindow().<Spinner>getComponent("value").setMaxValue(val);
                            spinner.getWindow().<ProgressBar>getComponent("progress").setEnd(val);
                        }))
                .spinner("value", 50, 0, 100, 1, spinner -> spinner
                        .orientAdvanced(adv -> adv
                                .copyX("max")
                                .below("max"))
                        .minDimensionsAreValueSize()
                        .setPadding(2)
                        .addChangeListener(val -> spinner.getWindow().<ProgressBar>getComponent("progress").setProgress(val)))
                .checkBox("infinite", checkBox -> checkBox
                        .orientAdvanced(adv -> adv
                                .copyX("max")
                                .below("value"))
                        .minDimensionsAreValueSize()
                        .setPadding(2)
                        .addSelectionListener(state -> checkBox.getWindow().<ProgressBar>getComponent("progress").setInfinite(state)))
                .progressBar("progress", 100, progressBar -> progressBar
                        .setProgress(50)
                        .orientAdvanced(adv -> adv
                                .x(0.05d)
                                .width(0.9d)
                                .configureAxis(Axis.Y, calc -> calc
                                        .min(DistUnit.MULT, 0.95d, Axis.HEIGHT, DistUnit.PX, -30)
                                        .ease(DistUnit.MULT, 0.1d, Axis.HEIGHT))
                                .configureAxis(Axis.HEIGHT, calc -> calc
                                        .min(DistUnit.PX, 30)
                                        .ease(DistUnit.MULT, 0.1d, Axis.HEIGHT))))
                .label("maxLabel", "Maximum progress", label -> label
                        .orientAdvanced(adv -> adv
                                .right("max", "value", "infinite")
                                .copyY("max"))
                        .minDimensionsAreValueSize()
                        .setPadding(2))
                .show();
    }

    private static PIcon filledImage(int rgb) {
        PImage img = new DirectImageRGB(32, 32);
        img.fillRGB(rgb);
        return img;
    }
}
