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

package net.daporkchop.lib.gui.component.type.functional;

import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.state.functional.ProgressBarState;

/**
 * @author DaPorkchop_
 */
public interface ProgressBar extends Component<ProgressBar, ProgressBarState> {
    @Override
    default ProgressBarState getState() {
        return this.isVisible() ?
                this.isEnabled() ?
                        this.isHovered() ? ProgressBarState.ENABLED_HOVERED : ProgressBarState.ENABLED
                        : this.isHovered() ? ProgressBarState.DISABLED_HOVERED : ProgressBarState.DISABLED
                : ProgressBarState.HIDDEN;
    }

    int getProgress();
    ProgressBar setProgress(int progress);
    int getEnd();
    ProgressBar setEnd(int end);

    default ProgressBar step() { return this.setProgress(this.getProgress() + 1); }
    default ProgressBar step(int step) { return this.setProgress(this.getProgress() + step); }

    boolean isInfinite();
    ProgressBar setInfinite(boolean infinite);
}
