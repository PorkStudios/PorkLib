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

package minecraft.java;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.pool.array.ArrayAllocator;
import net.daporkchop.lib.minecraft.format.anvil.AnvilSaveFormat;
import net.daporkchop.lib.minecraft.format.java.AnvilPooledArrayAllocator;
import net.daporkchop.lib.minecraft.save.Save;
import net.daporkchop.lib.minecraft.save.SaveOptions;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.util.WriteAccess;
import net.daporkchop.lib.minecraft.world.Section;
import net.daporkchop.lib.minecraft.world.World;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author DaPorkchop_
 */
public class LevelDatParserTest {
    public static File ROOT;

    public static final String[] VERSIONS = {
            "1_8_9",
            "1_12_2",
            "1_13_2"
    };

    @BeforeClass
    public static void extractSaves() throws IOException {
        ROOT = File.createTempFile("porklib-minecraft-tests", "txt").getAbsoluteFile();
        ROOT.delete();
        ROOT = new File(ROOT.getParentFile(), "porklib-minecraft-tests" + System.nanoTime());
        for (String version : VERSIONS) {
            try (ZipInputStream in = new ZipInputStream(LevelDatParserTest.class.getResourceAsStream(version + ".zip"))) {
                for (ZipEntry entry = in.getNextEntry(); entry != null; entry = in.getNextEntry()) {
                    if (!entry.isDirectory()) {
                        try (DataOut out = DataOut.wrap(PFiles.ensureFileExists(new File(ROOT, entry.getName())))) {
                            out.transferFrom(DataIn.wrap(in));
                        }
                    }
                    in.closeEntry();
                }
            }
        }
    }

    @AfterClass
    public static void deleteSaves() throws IOException {
        PFiles.rm(ROOT);
    }

    @Test
    public void test() throws IOException {
        for (String version : VERSIONS) {
            System.out.printf("Opening minecraft world at %s...\n", new File(ROOT, version));
            try (Save save = new AnvilSaveFormat().open(new File(ROOT, version), SaveOptions.DEFAULT.clone()
                    .set(SaveOptions.ACCESS, WriteAccess.READ_ONLY)
                    .build())) {
                System.out.println(save.version() + " " + save.worlds().map(World::id).map(Identifier::toString).collect(Collectors.toList()));

                try (World world = save.world(Identifier.fromString("overworld"))) {
                    try (Section section = world.storage().loadSection(0, 0, 0)) {
                        System.out.println(section.getBlockRuntimeId(0, 0, 0));
                        System.out.println(section.blockRegistry().getState(section.getBlockRuntimeId(0, 0, 0)));
                        System.out.println(section.getBlockState(0, 0, 0));
                        System.out.println(section.blockRegistry()
                                .getDefaultState(section.getBlockLegacyId(0, 0, 0))
                                .withMeta(section.getBlockMeta(0, 0, 0)));
                    }
                }
            }
        }
    }

    @Test
    public void loadTestWorld1_8_9() throws IOException {
        this.loadTestWorld("1_8_9");
    }

    @Test
    public void loadTestWorld1_12_2() throws IOException {
        this.loadTestWorld("1_12_2");
    }

    private void loadTestWorld(@NonNull String name)   throws IOException {
        try (Save save = new AnvilSaveFormat().open(new File(ROOT, name), SaveOptions.DEFAULT.clone()
                .set(SaveOptions.ACCESS, WriteAccess.READ_ONLY)
                .set(SaveOptions.BYTE_ALLOC, new AnvilPooledArrayAllocator(ArrayAllocator.unpooled(byte.class), 32, 32))
                .build())) {
            try (World world = save.world(Identifier.fromString("overworld"))) {
                try (Section section = world.storage().loadSection(0, 4, 0)) {
                    section.tileEntities().forEach(System.out::println);
                }
            }
        }
    }
}