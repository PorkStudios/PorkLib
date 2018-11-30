/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.network.util.reliability;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Allows specifying the reliability that a packet will arrive at its destination.
 * <p>
 * Note: when using TCP, this has no effect. The obvious reason is that TCP is effectively {@link Reliability#RELIABLE_ORDERED} :P
 *
 * @author DaPorkchop_
 */
public enum Reliability {
    /**
     * No guarantees that the data will arrive
     */
    UNRELIABLE,
    /**
     * No guarantees that the data will arrive, however when it arrives, older packets will be discarded
     */
    UNRELIABLE_SEQUENCED,
    /**
     * Guarantees that the packets will arrive
     */
    RELIABLE,
    /**
     * Guarantees that the packets will arrive, however when they arrive, older packets will be discarded
     */
    RELIABLE_SEQUENCED,
    /**
     * Guarantees that the packets will arrive and be processed in the order they were sent in
     */
    RELIABLE_ORDERED;

    public static final Collection<Reliability> NONE = Collections.emptyList();

    public static final Collection<Reliability> ALL = Arrays.asList(
            UNRELIABLE,
            UNRELIABLE_SEQUENCED,
            RELIABLE,
            RELIABLE_SEQUENCED,
            RELIABLE_ORDERED
    );
}
