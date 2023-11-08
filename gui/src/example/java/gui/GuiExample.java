/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package gui;

import lombok.NonNull;
import net.daporkchop.lib.imaging.bitmap.PIcon;
import net.daporkchop.lib.imaging.bitmap.PImage;
import net.daporkchop.lib.imaging.bitmap.image.DirectImageRGB;
import net.daporkchop.lib.gui.GuiEngine;
import net.daporkchop.lib.gui.component.orientation.advanced.Axis;
import net.daporkchop.lib.gui.component.orientation.advanced.calculator.DistUnit;
import net.daporkchop.lib.gui.component.state.functional.ButtonState;
import net.daporkchop.lib.gui.component.state.functional.CheckBoxState;
import net.daporkchop.lib.gui.component.state.functional.LabelState;
import net.daporkchop.lib.gui.component.type.Window;
import net.daporkchop.lib.gui.component.type.functional.ProgressBar;
import net.daporkchop.lib.gui.component.type.functional.Slider;
import net.daporkchop.lib.gui.component.type.functional.Table;
import net.daporkchop.lib.gui.util.Alignment;
import net.daporkchop.lib.gui.util.ScrollCondition;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static net.daporkchop.lib.logging.Logging.*;

/**
 * @author DaPorkchop_
 */
public class GuiExample {
    public static final String    LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
    public static final GuiEngine ENGINE      = GuiEngine.swing();

    public static void main(String... args) {
        ENGINE.newWindow(64, 64, 512, 256)
                .setTitle("Example GUI").setIcon(randomImage(32, 32), randomImage(16, 16))
                .button("button1", button -> button
                        .orientRelative(0.3d, 0.15d, 0.4d, 0.1d)
                        .setText("Example Button!")
                        .setTooltip("This is a tooltip that will be shown when hovering the mouse over the button.")
                        .setTextPos(Alignment.TOP_LEFT)
                        .setTextColor(0xFF00AAAA)
                        .setIcon(filledImage(0xFF0000))
                        .setIcon(ButtonState.ENABLED_HOVERED, filledImage(0x0000FF))
                        .setIcon(ButtonState.ENABLED_CLICKED, filledImage(0x00FF00))
                        .addStateListener(state -> logger.debug("%s changed state: %s", button.getName(), state.name()))
                        .setClickHandler((mouseButton, x, y) -> button.getWindow().getComponent("label2").toggle())
                        .minDimensionsAreValueSize())
                .button("button2", button -> button
                        .orientRelative(0, 0.0d, 0.1d, 0.1d)
                        .setClickHandler((mouseButton, x, y) -> logger.info("Bounds: %s", button.getWindow().getComponent("panel.button2").getBounds())))
                .label("label1", "<html><span style=\"font-weight: bold\">" + LOREM_IPSUM + "</span></html>", label -> label
                        .orientRelative(0.5d, 0, 0.5d, 0.1d)
                        .setTooltip("This is a label. Labels can only display plain text.")
                        .setTextColor(0xFFFF00FF))
                .label("label2", label -> label
                        .orientRelative(0.8d, 0.5d, 0.1d, 0.1d)
                        .setIcon(filledImage(0x00FF00))
                        .addEnableListener(() -> logger.info("%s was enabled!", label.getName()))
                        .setIcon(LabelState.DISABLED, filledImage(0xFF0000)))
                .button("button3", button -> button
                        .orientAdvanced(orientation -> orientation
                                .configureAxis(Axis.X, calc -> calc.ease(DistUnit.PX, 0))
                                .configureAxis(Axis.Y, calc -> calc.ease(DistUnit.MULT, 0.5, Axis.HEIGHT)
                                        .min(DistUnit.PX, 50, DistUnit.MULT, 0.5, Axis.WIDTH))
                                .configureAxis(Axis.WIDTH, calc -> calc.ease(DistUnit.PX, 30))
                                .configureAxis(Axis.HEIGHT, Axis.WIDTH)))
                .scrollPane("scrollPane1", scrollPane -> scrollPane
                        .orientRelative(0.2d, 0.5d, 0.6d, 0.4d)
                        .setScrolling(ScrollCondition.ALWAYS)
                        //.minDimensionsAreValueSize()
                        .label("label1", LOREM_IPSUM, label -> label
                                .orientRelative(0.0d, 0.0d, 0.75d, 0.15d)
                                .minDimensionsAreValueSize())
                        .label("label2", label -> label
                                .orientRelative(0.0d, 0.75d, 0.5d, 0.75d)
                                .setColor(0xFF5555FF)
                                .setText("Hello World!")))
                .button("button4", button -> button
                        .orientRelative(0.8d, 0.6d, 0.2d, 0.1d)
                        .setText("Dropdown test")
                        .setClickHandler((mouseButton, x, y) -> displayDropdownTestWindow(button.getWindow())))
                .button("button5", button -> button
                        .orientRelative(0.8d, 0.7d, 0.2d, 0.1d)
                        .setText("Scrollbar test")
                        .setClickHandler((mouseButton, x, y) -> displayScrollbarTestWindow(button.getWindow())))
                .button("button6", button -> button
                        .orientRelative(0.8d, 0.8d, 0.2d, 0.1d)
                        .setText("Form test")
                        .setClickHandler((mouseButton, x, y) -> FormExample.displayForm(button.getWindow())))
                .button("button7", button -> button
                        .orientRelative(0.8d, 0.9d, 0.2d, 0.1d)
                        .setText("Table test")
                        .setClickHandler((mouseButton, x, y) -> displayTableTestWindow(button.getWindow())))
                .addStateListener(state -> logger.debug("Window changed state: %s", state))
                .addVisibleListener(() -> logger.info("Window is now visible!"))
                .show();
    }

    public static void displayDropdownTestWindow(@NonNull Window parentWindow) {
        parentWindow.popup(128, 128, 512, 300)
                .setTitle("Dropdown menu test")
                .dropdown("dropdown1", ExampleEnum.class, dropdown -> dropdown
                        .orientRelative(0.05d, 0.05d, 0.2d, 0.1d)
                        .minDimensionsAreValueSize()
                        .addValueSelectedListener(value -> logger.info("Selected value changed to \"%s\"!", value.name())))
                .checkBox("checkBox1", checkBox -> checkBox
                        .orientAdvanced(adv -> adv
                                .configureAxis(Axis.X, calc -> calc.ease(DistUnit.MULT, 0.05d, Axis.WIDTH))
                                .configureAxis(Axis.Y, calc -> calc.min(DistUnit.RELATIVE, "dropdown1", Axis.BELOW)))
                        .minDimensionsAreValueSize()
                        .setText("Dummy text box 1")
                        .setIcon(CheckBoxState.ENABLED_HOVERED_SELECTED, randomImage(32, 32))
                        .addSelectionListener(selected -> logger.info("Checkbox %sselected!", selected ? "" : "de")))
                .checkBox("checkBox2", checkBox -> checkBox
                        .orientAdvanced(adv -> adv
                                .configureAxis(Axis.X, calc -> calc.ease(DistUnit.MULT, 0.05d, Axis.WIDTH))
                                .configureAxis(Axis.Y, calc -> calc.min(DistUnit.RELATIVE, "checkBox1", Axis.BELOW)))
                        .minDimensionsAreValueSize()
                        .setText("Text box 2!")
                        .setIcon(randomImage(32, 32)))
                .radioGroupFast("group1")
                .radioButton("radioButton1", "group1", button -> button
                        .orientAdvanced(adv -> adv
                                .configureAxis(Axis.X, calc -> calc.min(DistUnit.RELATIVE, "dropdown1", Axis.RIGHT)
                                        .min(DistUnit.RELATIVE, "checkBox1", Axis.RIGHT)
                                        .min(DistUnit.RELATIVE, "checkBox2", Axis.RIGHT))
                                .configureAxis(Axis.Y, calc -> calc.ease(DistUnit.MULT, 0.05d, Axis.HEIGHT)))
                        .minDimensionsAreValueSize()
                        .setText("Radio button 1")
                        .addSelectionListener(selected -> logger.info("Radio button 1 %sselected!", selected ? "" : "de")))
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
                        .addChangeListener(value -> logger.info("Spinner value changed to %d!", value)))
                .show();
    }

    public static void displayScrollbarTestWindow(@NonNull Window parentWindow) {
        parentWindow.popup(128, 128, 512, 300)
                .setTitle("Scrollbar test")
                .spinner("max", 100.5d, 0.75d, Double.POSITIVE_INFINITY, 0.05d, spinner -> spinner
                        .orientAdvanced(adv -> adv
                                .x(0.05d)
                                .y(0.05d))
                        .minDimensionsAreValueSize().pad(2)
                        .addChangeListener(val -> {
                            spinner.getWindow().<Slider>getComponent("value").setMaxValue(val);
                            spinner.getWindow().<ProgressBar>getComponent("progress").setEnd(val);
                        }))
                .slider("value", 50, 0, 100, slider -> slider
                        .orientAdvanced(adv -> adv.belowAndCopyX("max"))
                        .minDimensionsAreValueSize().pad(2)
                        .addChangeListener(val -> slider.getWindow().<ProgressBar>getComponent("progress").setProgress(val)))
                .label("valueDisplay", label -> label
                        .orientAdvanced(adv -> adv.rightAndCopyYAndHeight("value"))
                        .minDimensionsAreValueSize().pad(2)
                        .trackSlider("value"))
                .checkBox("infinite", checkBox -> checkBox
                        .orientAdvanced(adv -> adv.belowAndCopyX("value"))
                        .minDimensionsAreValueSize().pad(2)
                        .addSelectionListener(state -> checkBox.getWindow().<ProgressBar>getComponent("progress").setInfinite(state)))
                .textBox("text", "Some text!", textBox -> textBox
                        .orientAdvanced(adv -> adv
                                .belowAndCopyX("infinite")
                                .copyWidth("password"))
                        .minDimensionsAreValueSize().pad(2)
                        .addTextChangedListener(text -> logger.info("Text changed to: \"%s\"!", text)))
                .passwordBox("password", "securePassword123", textBox -> textBox
                        .orientAdvanced(adv -> adv.belowAndCopyX("text"))
                        .minDimensionsAreValueSize().pad(2)
                        .addTextChangedListener(text -> logger.info("Password changed to: \"%s\"!", text)))
                .progressBar("progress", 100, progressBar -> progressBar
                        .setProgress(50)
                        .orientAdvanced(adv -> adv
                                .x(0.05d)
                                .width(0.9d)
                                .configureAxis(Axis.Y, calc -> calc
                                        .min(DistUnit.RELATIVE, "password", Axis.BELOW)
                                        .min(DistUnit.MULT, 0.95d, Axis.HEIGHT, DistUnit.PX, -30)
                                        .ease(DistUnit.MULT, 0.1d, Axis.HEIGHT))
                                .configureAxis(Axis.HEIGHT, calc -> calc
                                        .min(DistUnit.PX, 30)
                                        .ease(DistUnit.MULT, 0.1d, Axis.HEIGHT)))
                        .pad(2))
                .label("maxLabel", "Maximum progress", label -> label
                        .orientAdvanced(adv -> adv
                                .right("max", "value", "valueDisplay", "infinite", "text", "password")
                                .copyYAndHeight("max"))
                        .minDimensionsAreValueSize().pad(2).padLeft(10))
                .label("valueLabel", "Progress", label -> label
                        .orientAdvanced(adv -> adv
                                .copyX("maxLabel")
                                .copyYAndHeight("value"))
                        .minDimensionsAreValueSize().pad(2))
                .label("infiniteLabel", "Infinite", label -> label
                        .orientAdvanced(adv -> adv
                                .copyX("maxLabel")
                                .copyYAndHeight("infinite"))
                        .minDimensionsAreValueSize().pad(2))
                .show();
    }

    public static void displayTableTestWindow(@NonNull Window parentWindow) {
        parentWindow.popup(128, 128, 512, 300)
                .setTitle("Table test")
                .table("table1", table -> {
                    table.orientRelative(0, 0, 1.0d, 0.7d);
                    for (int c = 0; c < 3; c++) {
                        table.addAndGetColumn(String.format("col-%d", c), Integer.class, (value, label, row, col) -> label.setText(String.valueOf(value)));
                    }
                    for (int r = 0; r < 5; r++) {
                        Table.Row row = table.addAndGetRow();
                        for (int c = 0; c < 3; c++) {
                            row.setValue(c, ThreadLocalRandom.current().nextInt(1000));
                        }
                    }
                    table.<Integer>getColumn(2)
                            .setClickHandler((value, row, col, mouseButton) -> logger.info("Clicked value %d in row %d!", value, row.index()));
                })
                .checkBox("toggleHeader", checkBox -> checkBox
                        .orientRelative(0, 0.9d, 0.25d, 0.1d)
                        .setText("Header")
                        .setSelected(true)
                        .addSelectionListener(checkBox.getWindow().<Table>getChild("table1")::setHeadersShown))
                .show();
    }

    private static PIcon filledImage(int rgb) {
        PImage img = new DirectImageRGB(32, 32);
        for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 32; y++) {
                img.setRGB(x, y, rgb);
            }
        }
        return img.unsafeImmutableView();
    }

    private static PIcon randomImage(int width, int height) {
        PImage img = new DirectImageRGB(width, height);
        Random random = ThreadLocalRandom.current();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                img.setRGB(x, y, random.nextInt() >>> 8);
            }
        }
        return img.unsafeImmutableView();
    }
}
