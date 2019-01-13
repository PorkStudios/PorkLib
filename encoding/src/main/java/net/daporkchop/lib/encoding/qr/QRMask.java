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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.encoding.util.func.IntBiPredicate;

import java.util.BitSet;
import java.util.function.IntBinaryOperator;

/**
 * See https://upload.wikimedia.org/wikipedia/commons/4/49/QRCode-4-Levels%2CMasks.png
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public enum QRMask implements IntBiPredicate {
    /**
     * #_#_#_#_#_
     * _#_#_#_#_#
     * #_#_#_#_#_
     * _#_#_#_#_#
     * #_#_#_#_#_
     * _#_#_#_#_#
     * #_#_#_#_#_
     * _#_#_#_#_#
     * #_#_#_#_#_
     * _#_#_#_#_#
     */
    MASK_0((row, col) -> (row + col) % 2 == 0),
    /**
     * ##########
     * __________
     * ##########
     * __________
     * ##########
     * __________
     * ##########
     * __________
     * ##########
     * __________
     */
    MASK_1((row, col) -> row % 2 == 0),
    /**
     * #__#__#__#
     * #__#__#__#
     * #__#__#__#
     * #__#__#__#
     * #__#__#__#
     * #__#__#__#
     * #__#__#__#
     * #__#__#__#
     * #__#__#__#
     * #__#__#__#
     */
    MASK_2((row, col) -> col % 3 == 0),
    /**
     * #__#__#__#
     * __#__#__#_
     * _#__#__#__
     * #__#__#__#
     * __#__#__#_
     * _#__#__#__
     * #__#__#__#
     * __#__#__#_
     * _#__#__#__
     * #__#__#__#
     */
    MASK_3((row, col) -> (row + col) % 3 == 0),
    /**
     * ###___###_
     * ###___###_
     * ___###___#
     * ___###___#
     * ###___###_
     * ###___###_
     * ___###___#
     * ___###___#
     * ###___###_
     * ###___###_
     */
    MASK_4((row, col) -> (row / 2 + col / 3) % 2 == 0),
    /**
     * ##########
     * #_____#___
     * #__#__#__#
     * #_#_#_#_#_
     * #__#__#__#
     * #_____#___
     * ##########
     * #_____#___
     * #__#__#__#
     * #_#_#_#_#_
     */
    MASK_5((row, col) -> (row * col) % 2 + (row * col) % 3 == 0),
    MASK_6((row, col) -> ((row * col) % 2 + (row * col) % 3) % 2 == 0),
    MASK_7((row, col) -> ((row + col) % 2 + (row + col) % 3) % 2 == 0);

    @NonNull
    private final IntBiPredicate func;

    @Override
    public boolean test(int x, int y) {
        return this.func.test(x, y);
    }

    /**
     * Gets a mask grid to be XOR-ed with the data
     * @param size the size of the grid to make
     * @return a grid
     */
    public BitSet grid(int size)    {
        BitSet bitSet = new BitSet(size * size);
        for (int x = size - 1; x >= 0; x--) {
            for (int y = size - 1; y >= 0; y--) {
                if (this.func.test(x, y))   {
                    bitSet.set(x * size + y);
                }
            }
        }
        return bitSet;
    }
}
