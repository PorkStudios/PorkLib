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

package net.daporkchop.lib.minecraft.item;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.util.Identifier;

/**
 * Representation of a potion effect.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent = true, chain = true)
public class PotionEffect {
    public static final Identifier EMPTY_EFFECT_ID = Identifier.fromString("minecraft:empty");

    /**
     * The ID of the potion effect.
     */
    @NonNull
    protected Identifier id;

    /**
     * The amplifier of the effect.
     * <p>
     * Values less than {@code 1} are treated as {@code 1}.
     */
    protected int amplifier = 1;

    /**
     * The duration of the effect in ticks.
     * <p>
     * Values less than {@code 1} are treated as {@code 1}.
     */
    protected int duration = 1;

    /**
     * Whether or not this potion effect was produced by a beacon.
     */
    protected boolean ambient = false;

    /**
     * Whether or not this potion effect should show particles while active.
     */
    protected boolean showParticles = true;

    /**
     * Whether or not the potion effect's icon should be shown on-screen.
     */
    protected boolean showIcon = true;
}
