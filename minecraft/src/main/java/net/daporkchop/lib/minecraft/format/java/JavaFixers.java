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

package net.daporkchop.lib.minecraft.format.java;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.compat.datafix.DataFixer;
import net.daporkchop.lib.minecraft.format.anvil.version.chunk.decoder.FlattenedChunkDecoder;
import net.daporkchop.lib.minecraft.format.java.section.JavaSection;
import net.daporkchop.lib.minecraft.format.java.version.section.decoder.LegacySectionDecoder;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.nbt.tag.CompoundTag;

/**
 * A collection of multiple {@link DataFixer}s used by a Java edition save for NBT serialization of various things.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class JavaFixers {
    private static final class Default {
        private static final JavaFixers DEFAULT_JAVA_FIXERS;

        static {
            DEFAULT_JAVA_FIXERS = new JavaFixers(
                    DataFixer.<Chunk, CompoundTag, JavaVersion>builder()
                            //.addDecoder(LegacyChunkDecoder.VERSION, new LegacyChunkDecoder())
                            .addDecoder(FlattenedChunkDecoder.VERSION, new FlattenedChunkDecoder())
                            .build(),
                    DataFixer.<JavaSection, CompoundTag, JavaVersion>builder()
                            .addDecoder(LegacySectionDecoder.VERSION, new LegacySectionDecoder())
                            .build());
        }
    }

    /**
     * @return the default {@link JavaFixers}
     */
    public static JavaFixers defaultFixers() {
        return Default.DEFAULT_JAVA_FIXERS;
    }

    @NonNull
    protected final DataFixer<Chunk, CompoundTag, JavaVersion> chunk;

    @NonNull
    protected final DataFixer<JavaSection, CompoundTag, JavaVersion> section;
}
