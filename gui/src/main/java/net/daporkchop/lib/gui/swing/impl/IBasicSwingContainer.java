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

package net.daporkchop.lib.gui.swing.impl;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.Element;
import net.daporkchop.lib.gui.component.capability.ComponentAdder;
import net.daporkchop.lib.gui.component.orientation.advanced.Axis;
import net.daporkchop.lib.gui.component.state.ElementState;
import net.daporkchop.lib.gui.component.type.container.Panel;
import net.daporkchop.lib.gui.component.type.container.ScrollPane;
import net.daporkchop.lib.gui.component.type.functional.Button;
import net.daporkchop.lib.gui.component.type.functional.CheckBox;
import net.daporkchop.lib.gui.component.type.functional.Dropdown;
import net.daporkchop.lib.gui.component.type.functional.Label;
import net.daporkchop.lib.gui.component.type.functional.ProgressBar;
import net.daporkchop.lib.gui.component.type.functional.RadioButton;
import net.daporkchop.lib.gui.component.type.functional.Slider;
import net.daporkchop.lib.gui.component.type.functional.Spinner;
import net.daporkchop.lib.gui.component.type.functional.TextBox;
import net.daporkchop.lib.gui.component.type.misc.RadioButtonGroup;
import net.daporkchop.lib.gui.swing.type.container.SwingPanel;
import net.daporkchop.lib.gui.swing.type.container.SwingScrollPane;
import net.daporkchop.lib.gui.swing.type.functional.SwingButton;
import net.daporkchop.lib.gui.swing.type.functional.SwingCheckBox;
import net.daporkchop.lib.gui.swing.type.functional.SwingDropdown;
import net.daporkchop.lib.gui.swing.type.functional.SwingLabel;
import net.daporkchop.lib.gui.swing.type.functional.SwingPasswordBox;
import net.daporkchop.lib.gui.swing.type.functional.SwingProgressBar;
import net.daporkchop.lib.gui.swing.type.functional.SwingRadioButton;
import net.daporkchop.lib.gui.swing.type.functional.SwingSlider;
import net.daporkchop.lib.gui.swing.type.functional.SwingSpinner;
import net.daporkchop.lib.gui.swing.type.functional.SwingTextBox;
import net.daporkchop.lib.gui.swing.type.misc.SwingRadioButtonGroup;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import javax.swing.*;

/**
 * @author DaPorkchop_
 */
@SuppressWarnings("unchecked")
public interface IBasicSwingContainer<Impl extends Container, Swing extends java.awt.Container, State extends ElementState<? extends Element, State>> extends Container<Impl, State> {
    //componentadder methods
    @Override
    default Panel panel(@NonNull String name) {
        SwingPanel panel = new SwingPanel(name);
        this.addChild(panel);
        return panel;
    }

    @Override
    default ScrollPane scrollPane(@NonNull String name) {
        SwingScrollPane scrollPane = new SwingScrollPane(name);
        this.addChild(scrollPane);
        return scrollPane;
    }

    @Override
    default Button button(@NonNull String name) {
        SwingButton button = new SwingButton(name);
        this.addChild(button);
        return button;
    }

    @Override
    default CheckBox checkBox(@NonNull String name) {
        SwingCheckBox checkBox = new SwingCheckBox(name);
        this.addChild(checkBox);
        return checkBox;
    }

    @Override
    default <V> Dropdown<V> dropdown(@NonNull String name) {
        SwingDropdown<V> dropdown = new SwingDropdown<>(name);
        this.addChild(dropdown);
        return dropdown;
    }

    @Override
    default Label label(@NonNull String name) {
        SwingLabel label = new SwingLabel(name);
        this.addChild(label);
        return label;
    }

    @Override
    default ProgressBar progressBar(@NonNull String name) {
        SwingProgressBar progressBar = new SwingProgressBar(name);
        this.addChild(progressBar);
        return progressBar;
    }

    @Override
    default RadioButton radioButton(@NonNull String name, @NonNull RadioButtonGroup group) {
        SwingRadioButton radioButton = new SwingRadioButton(name, (SwingRadioButtonGroup) group);
        this.addChild(radioButton);
        return radioButton;
    }

    @Override
    default RadioButton radioButton(@NonNull String name, @NonNull String groupName) {
        return this.radioButton(name, this.<RadioButtonGroup>getChild(groupName));
    }

    @Override
    default Slider slider(@NonNull String name) {
        SwingSlider slider = new SwingSlider(name);
        this.addChild(slider);
        return slider;
    }

    @Override
    default Spinner spinner(@NonNull String name) {
        SwingSpinner spinner = new SwingSpinner(name);
        this.addChild(spinner);
        return spinner;
    }

    @Override
    default TextBox textBox(@NonNull String name) {
        SwingTextBox textBox = new SwingTextBox(name);
        this.addChild(textBox);
        return textBox;
    }

    @Override
    default TextBox passwordBox(@NonNull String name) {
        SwingPasswordBox passwordBox = new SwingPasswordBox(name);
        this.addChild(passwordBox);
        return passwordBox;
    }

    @Override
    default RadioButtonGroup radioGroup(@NonNull String name) {
        SwingRadioButtonGroup radioButtonGroup = new SwingRadioButtonGroup(name);
        this.addChild(radioButtonGroup);
        return radioButtonGroup;
    }

    //container methods
    @Override
    default Impl addChild(@NonNull Component child, boolean update) {
        if (!(child instanceof SwingComponent))    {
            throw new IllegalArgumentException(String.format("Invalid child type! Expected %s but found %s!", SwingComponent.class.getCanonicalName(), child.getClass().getCanonicalName()));
        } else if (this.getChildren().containsKey(child.getName()))  {
            throw new IllegalArgumentException(String.format("Child with name %s exists!", child.getName()));
        }
        SwingComponent swing = (SwingComponent) child;
        this.getChildren().put(child.getName(), swing.setParent(this));
        if (swing.hasSwing())   {
            this.getSwing().add(swing.swing);
            return update ? this.update() : (Impl) this;
        } else {
            return (Impl) this;
        }
    }

    @Override
    default Impl removeChild(@NonNull String name, boolean update) {
        SwingComponent removed = (SwingComponent) this.getChildren().remove(name);
        if (removed == null) {
            throw new IllegalArgumentException(String.format("No such child: %s", name));
        } else {
            this.getSwing().remove(removed.swing);
            return update ? this.update() : (Impl) this;
        }
    }

    //other
    Swing getSwing();

    default BoundingBox computeMinBounds() {
        if (this.isClampedToValueMinSizes()) {
            int maxW = 0;
            int maxH = 0;
            for (Component element : this.getChildren().values()) {
                BoundingBox bb = element.getMinBounds();
                if (bb != null) {
                    int i = Axis.RIGHT.getFrom(bb, element, null);
                    if (i > maxW) {
                        maxW = i;
                    }
                    i = Axis.BELOW.getFrom(bb, element, null);
                    if (i > maxH) {
                        maxH = i;
                    }
                }
            }
            return new BoundingBox(this.getBounds().getX(), this.getBounds().getY(), maxW, maxH);
        } else {
            return null;
        }
    }
}
