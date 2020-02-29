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
 * A scroll pane is similar to a {@link Panel} in that it's one of the simplest possible containers there
 * is. The main difference with a scroll pane is that elements can be positioned anywhere in the container,
 * and/or extend beyond the limits of the container, and the pane will display scroll bars on the right and
 * bottom for navigating the full extent of it's components.
 *
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

    default ScrollPane setScrollSpeed(int value) {
        return this.setScrollSpeed(new ConstantValueScrollSpeedCalculator(value));
    }

    ScrollPane setScrollSpeed(@NonNull ScrollSpeedCalculator calculator);

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

    default ScrollPane setScrollSpeed(@NonNull ScrollDir dir, int value) {
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
    class BiDirectionalScrollSpeedCalculator implements ScrollSpeedCalculator {
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
    class ConstantValueScrollSpeedCalculator implements ScrollSpeedCalculator {
        protected final int speed;

        @Override
        public int getScrollSpeed(@NonNull ScrollDir dir, @NonNull BoundingBox bounds) {
            return this.speed;
        }
    }
}
