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

package net.daporkchop.lib.graphics;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public abstract class AbstractImageProducer implements ImageProducer {
    protected final Set<ImageConsumer> consumers = new HashSet<>();

    protected final int width;
    protected final int height;
    protected Hashtable properties = new Hashtable();

    @Override
    public void addConsumer(@NonNull ImageConsumer ic) {
        if (this.consumers.add(ic)) {
            this.initConsumer(ic);
            this.sendPixels(ic, 0, 0, this.width, this.height);
            if (this.isConsumer(ic)) {
                /*ic.imageComplete(animating
                        ? ImageConsumer.SINGLEFRAMEDONE
                        : ImageConsumer.STATICIMAGEDONE);*/
                ic.imageComplete(ImageConsumer.STATICIMAGEDONE);
                if (/*!animating && */this.isConsumer(ic)) {
                    ic.imageComplete(ImageConsumer.IMAGEERROR);
                    this.removeConsumer(ic);
                }
            }
        }
    }

    @Override
    public boolean isConsumer(@NonNull ImageConsumer ic) {
        return this.consumers.contains(ic);
    }

    @Override
    public void removeConsumer(@NonNull ImageConsumer ic) {
        this.consumers.remove(ic);
    }

    @Override
    public void startProduction(@NonNull ImageConsumer ic) {
        this.addConsumer(ic);
    }

    @Override
    public void requestTopDownLeftRightResend(ImageConsumer ic) {
    }

    protected void initConsumer(@NonNull ImageConsumer ic) {
        if (this.isConsumer(ic)) {
            ic.setDimensions(this.width, this.height);
        }
        if (this.isConsumer(ic)) {
            ic.setProperties(this.properties);
        }
        if (this.isConsumer(ic)) {
            ic.setColorModel(this.getColorModel());
        }

        if (this.isConsumer(ic)) {
        /*ic.setHints(animating
                ? (fullbuffers
                ? (ImageConsumer.TOPDOWNLEFTRIGHT |
                ImageConsumer.COMPLETESCANLINES)
                : ImageConsumer.RANDOMPIXELORDER)
                : (ImageConsumer.TOPDOWNLEFTRIGHT |
                ImageConsumer.COMPLETESCANLINES |
                ImageConsumer.SINGLEPASS |
                ImageConsumer.SINGLEFRAME));*/
            ic.setHints(ImageConsumer.TOPDOWNLEFTRIGHT | ImageConsumer.COMPLETESCANLINES | ImageConsumer.SINGLEPASS | ImageConsumer.SINGLEFRAME);
        }
    }

    protected abstract ColorModel getColorModel();

    protected abstract void sendPixels(@NonNull ImageConsumer ic, int x, int y, int w, int h);
}
