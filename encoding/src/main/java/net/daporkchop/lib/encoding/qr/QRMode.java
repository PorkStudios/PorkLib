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
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.function.PFunctions;

import java.util.function.Function;

/**
 * Encoding modes supported by a QR code
 *
 * Currently mostly unimplemented
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public enum QRMode {
    NUMERIC(0b0001, PFunctions.throwing(UnsupportedOperationException::new)),
    ALPHANUMERIC(0b0010, PFunctions.throwing(UnsupportedOperationException::new)),
    BYTE(0b0100, b -> b),
    KANJI(0b0010, PFunctions.throwing(UnsupportedOperationException::new)),
    STRUCTURED_APPEND(0b0011, PFunctions.throwing(UnsupportedOperationException::new)),
    EXTENDED_CHANNEL_INTERPRETATION(0b0111, PFunctions.throwing(UnsupportedOperationException::new)),
    FNC1_IN_FIRST_POSITION(0b0101, PFunctions.throwing(UnsupportedOperationException::new)),
    FNC1_IN_SECOND_POSITION(0b1001, PFunctions.throwing(UnsupportedOperationException::new)),
    END_OF_MESSAGE(0b0000, PFunctions.throwing(UnsupportedOperationException::new));

    private final int id;

    @NonNull
    private final Function<byte[], Object> decoder;
}
