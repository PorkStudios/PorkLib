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
import lombok.ToString;
import net.daporkchop.lib.gui.component.type.Window;
import net.daporkchop.lib.gui.form.annotation.FormComponentName;
import net.daporkchop.lib.gui.form.annotation.FormTooltip;
import net.daporkchop.lib.gui.form.annotation.FormType;
import net.daporkchop.lib.logging.Logging;

/**
 * @author DaPorkchop_
 */
public class FormExample implements Logging {
    public static void displayForm(@NonNull Window parentWindow) {
        parentWindow.popup(128, 128, 512, 300)
                .setTitle("Form test")
                .spinner("spinner", spinner -> spinner
                        .orientRelative(2, 2, 0.0d, 0.0d)
                        .minDimensionsAreValueSize().pad(2))
                .spinner("spinner2", spinner -> spinner
                        .orientAdvanced(adv -> adv.belowAndCopyX("spinner"))
                        .minDimensionsAreValueSize().pad(2))
                .slider("slider", slider -> slider
                        .orientAdvanced(adv -> adv.belowAndCopyX("spinner2"))
                        .minDimensionsAreValueSize().pad(2))
                .button("complete", button -> button
                        .orientAdvanced(adv -> adv.belowAndCopyX("slider"))
                        .minDimensionsAreValueSize().pad(2)
                        .setText("Submit"))
                .scrollPane("sub", scrollPane -> scrollPane
                        .orientAdvanced(adv -> adv
                                .belowAndCopyX("complete")
                                .width(0.5d).height(0.25d))
                        .pad(2)
                        .checkBox("checkbox", checkBox -> checkBox
                                .orientRelative(2, 2, 0.0d, 0.0d)
                                .minDimensionsAreValueSize().pad(2)))
                .form(FormData.class, form -> form
                        .submitButton("complete")
                        .addListener((status, value) -> logger.info("Form completed with status: ${0}", status))
                        .addSuccessListener(value -> logger.info("${0}", value)))
                .show();
    }

    @ToString
    protected static class FormData {
        @FormType.Int(value = 27, max = 9001)
        @FormTooltip({
                "This value can be configured with a spinner.",
                "It may be set to at most 9001 and at least 0, and has a step size of 1."
        })
        public int spinner;

        @FormType.Int(50)
        @FormTooltip({
                "This is also a spinner, initialized by default to 50.\nIt's component is named differently than it's field."
        })
        @FormComponentName("spinner2")
        public int otherSpinner;

        @FormType.Int(type = FormType.Int.Type.SLIDER)
        @FormTooltip({
                "Sliders can be more useful than spinners in many cases!"
        })
        public int slider;

        public SubData sub;
    }

    @ToString
    protected static class SubData {
        @FormTooltip("This is a simple boolean value.")
        @FormComponentName("checkbox")
        public boolean flag;
    }
}
