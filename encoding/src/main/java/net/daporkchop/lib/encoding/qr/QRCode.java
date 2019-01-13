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

package net.daporkchop.lib.encoding.qr;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.math.primitive.PMath;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.BitSet;

import static java.lang.Math.sqrt;

/**
 * An actual QR code.
 * <p>
 * Contains methods for encoding the QR code as an image
 *
 * @author DaPorkchop_
 */
@Getter
public class QRCode {
    @NonNull
    protected final BitSet rawPixels;

    @NonNull
    protected final QRInfo info;

    public QRCode(@NonNull BitSet rawPixels, @NonNull QRInfo info) {
        this.rawPixels = rawPixels;
        this.info = info;

        int size = PMath.floorI(sqrt(rawPixels.size()));
        if (size != info.size) {
            throw new IllegalArgumentException(String.format("Invalid size! Expected %d but found %d!", size, info.size));
        }
    }

    /**
     * Encode this QR code as an image without padding
     *
     * @param scaleFactor the scale factor to use. 1 bit in the data will be scaled up to be this many pixels wide
     * @return a {@link BufferedImage} containing this QR code
     */
    public BufferedImage asImage(int scaleFactor) {
        return this.asImage(scaleFactor, 0);
    }

    /**
     * Encode this QR code as an image
     *
     * @param scaleFactor the scale factor to use. 1 bit in the data will be scaled up to be this many pixels wide
     * @param padding     additional padding to add to the image. The output image will have this many pixels of plain white on all sides
     * @return a {@link BufferedImage} containing this QR code
     */
    public BufferedImage asImage(int scaleFactor, int padding) {
        int qrSize = this.info.size; //the size of the QR code in squares
        int imgSize = 2 * padding + qrSize * scaleFactor; //the size of the image in pixels
        BufferedImage image = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_BYTE_BINARY);
        int[] arr; //copy buffer
        if (padding != 0) {
            //add padding
            arr = new int[padding];
            Arrays.fill(arr, 0xFFFFFF);
            for (int i = imgSize - 1; i >= 0; i--) {
                //sides
                image.setRGB(0, i, padding, 1, arr, 0, 0);
                image.setRGB(imgSize - padding, i, padding, 1, arr, 0, 0);
                //top+bottom
                image.setRGB(i, 0, 1, padding, arr, 0, 0);
                image.setRGB(i, imgSize - padding, 1, padding, arr, 0, 0);
            }
        }
        arr = new int[scaleFactor];
        for (int x = qrSize - 1; x >= 0; x--) {
            for (int y = qrSize - 1; y >= 0; y--) {
                Arrays.fill(arr, this.rawPixels.get(x * qrSize + y) ? 0xFFFFFF : 0); //fill buffer with correct color
                image.setRGB(padding + x * scaleFactor, padding + y * scaleFactor, scaleFactor, scaleFactor, arr, 0, 0);
            }
        }
        return image;
    }
}
