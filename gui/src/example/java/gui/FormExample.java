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
                .scrollPane("sub", scrollPane -> scrollPane
                        .orientAdvanced(adv -> adv
                                .belowAndCopyX("slider")
                                .width(0.5d).height(0.25d))
                        .pad(2)
                        .checkBox("checkbox", checkBox -> checkBox
                                .orientRelative(2, 2, 0.0d, 0.0d)
                                .minDimensionsAreValueSize().pad(2))
                        .dropdown("dropdown", dropdown -> dropdown
                                .orientAdvanced(adv -> adv.belowAndCopyX("checkbox"))
                                .minDimensionsAreValueSize().pad(2))
                        .radioGroupFast("radio")
                        .radioButton("TYPE_1", "radio", button -> button
                                .orientAdvanced(adv -> adv.belowAndCopyX("dropdown"))
                                .minDimensionsAreValueSize().pad(2))
                        .radioButton("TYPE_2", "radio", button -> button
                                .orientAdvanced(adv -> adv.belowAndCopyX("TYPE_1"))
                                .minDimensionsAreValueSize().pad(2))
                        .radioButton("TYPE_3", "radio", button -> button
                                .orientAdvanced(adv -> adv.belowAndCopyX("TYPE_2"))
                                .minDimensionsAreValueSize().pad(2))
                        .radioButton("JEFF", "radio", button -> button
                                .orientAdvanced(adv -> adv.belowAndCopyX("TYPE_3"))
                                .minDimensionsAreValueSize().pad(2)))
                .textBox("username", textBox -> textBox
                        .orientAdvanced(adv -> adv.belowAndCopyX("sub").width(0.25d))
                        .minDimensionsAreValueSize().pad(2))
                .passwordBox("password", textBox -> textBox
                        .orientAdvanced(adv -> adv.belowAndCopyXAndWidth("username"))
                        .minDimensionsAreValueSize().pad(2))
                .button("complete", button -> button
                        .orientAdvanced(adv -> adv.belowAndCopyX("password"))
                        .minDimensionsAreValueSize().pad(2)
                        .setText("Submit"))
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

        @FormType.Text(hint = "user@example.com")
        @FormTooltip("Your username to this amazing non-existent site!")
        public String username;

        @FormType.Text(type = FormType.Text.Type.PASSWORD)
        @FormTooltip("Your password to this amazing non-existent site!")
        public String password;
    }

    @ToString
    protected static class SubData {
        @FormTooltip("This is a simple boolean value.")
        @FormComponentName("checkbox")
        public boolean flag;

        public EnumValues dropdown;

        @FormType.Enum(value = 3, type = FormType.Enum.Type.RADIO_BUTTON)
        public EnumValues radio;

        enum EnumValues {
            TYPE_1,
            @FormType.EnumMemberTooltip({
                    "This is the second value in the enum",
                    "It has a tooltip which is only visible when used as a radio button!"
            })
            TYPE_2,
            TYPE_3,
            @FormType.EnumMemberName("jeff")
            @FormType.EnumMemberTooltip("name jeff lol")
            JEFF,;
        }
    }
}
