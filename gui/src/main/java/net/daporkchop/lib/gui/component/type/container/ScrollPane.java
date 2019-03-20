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

package net.daporkchop.lib.gui.component.type.container;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.gui.component.NestedContainer;
import net.daporkchop.lib.gui.component.state.container.ScrollPaneState;
import net.daporkchop.lib.gui.util.ScrollCondition;
import net.daporkchop.lib.gui.util.ScrollDir;
import net.daporkchop.lib.gui.util.math.BoundingBox;

/**
 * @author DaPorkchop_
 */
public interface ScrollPane extends NestedContainer<ScrollPane, ScrollPaneState> {
    int DEFAULT_SCROLL_SPEED = 25;

    @Override
    default ScrollPaneState getState() {
        return this.isVisible() ?
                this.isEnabled() ?
                        this.isHovered() ? ScrollPaneState.ENABLED_HOVERED : ScrollPaneState.ENABLED
                        : this.isHovered() ? ScrollPaneState.DISABLED_HOVERED : ScrollPaneState.DISABLED
                : ScrollPaneState.HIDDEN;
    }

    default ScrollPane setScrolling(@NonNull ScrollCondition condition) {
        return this.setScrolling(ScrollDir.HORIZONTAL, condition)
                .setScrolling(ScrollDir.VERTICAL, condition);
    }

    ScrollPane setScrolling(@NonNull ScrollDir dir, @NonNull ScrollCondition condition);

    ScrollCondition getScrolling(@NonNull ScrollDir dir);

    ScrollSpeedCalculator getScrollSpeed();
    ScrollPane setScrollSpeed(@NonNull ScrollSpeedCalculator calculator);
    default ScrollPane setScrollSpeed(int value)    {
        return this.setScrollSpeed(new ConstantValueScrollSpeedCalculator(value));
    }
    default ScrollPane setScrollSpeed(@NonNull ScrollDir dir, @NonNull ScrollSpeedCalculator calculator) {
        BiDirectionalScrollSpeedCalculator bi;
        if (this.getScrollSpeed() instanceof BiDirectionalScrollSpeedCalculator) {
            bi = (BiDirectionalScrollSpeedCalculator) this.getScrollSpeed();
        } else {
            this.setScrollSpeed(bi = new BiDirectionalScrollSpeedCalculator(this.getScrollSpeed(), this.getScrollSpeed()));
        }
        if (dir == ScrollDir.HORIZONTAL) {
            bi.setHorizontal(calculator);
        } else {
            bi.setVertical(calculator);
        }
        return this;
    }
    default ScrollPane setScrollSpeed(@NonNull ScrollDir dir, int value)    {
        return this.setScrollSpeed(dir, new ConstantValueScrollSpeedCalculator(value));
    }

    @FunctionalInterface
    interface ScrollSpeedCalculator {
        int getScrollSpeed(@NonNull ScrollDir dir, @NonNull BoundingBox bounds);
    }

    @FunctionalInterface
    interface MonodirectionScrollSpeedCalculator extends ScrollSpeedCalculator {
        @Override
        default int getScrollSpeed(@NonNull ScrollDir dir, @NonNull BoundingBox bounds) {
            return this.getScrollSpeed(bounds);
        }

        int getScrollSpeed(@NonNull BoundingBox bounds);
    }

    @RequiredArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Accessors(chain = true)
    class BiDirectionalScrollSpeedCalculator implements ScrollSpeedCalculator   {
        @NonNull
        protected ScrollSpeedCalculator horizontal;
        @NonNull
        protected ScrollSpeedCalculator vertical;

        @Override
        public int getScrollSpeed(@NonNull ScrollDir dir, @NonNull BoundingBox bounds) {
            return dir == ScrollDir.HORIZONTAL ? this.horizontal.getScrollSpeed(dir, bounds) : this.vertical.getScrollSpeed(dir, bounds);
        }
    }

    @RequiredArgsConstructor
    @Getter
    class ConstantValueScrollSpeedCalculator implements ScrollSpeedCalculator   {
        protected final int speed;

        @Override
        public int getScrollSpeed(@NonNull ScrollDir dir, @NonNull BoundingBox bounds) {
            return this.speed;
        }
    }
}
