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

import net.daporkchop.lib.db.DBBuilder;
import net.daporkchop.lib.db.DatabaseFormat;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.db.object.key.impl.HashKeyHasher;
import net.daporkchop.lib.db.object.serializer.impl.ByteArraySerializer;
import net.daporkchop.lib.hash.HashAlg;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
public class ArchiveTest {
    private final DatabaseFormat[] toTest = {
            DatabaseFormat.ZIP_TREE,
            DatabaseFormat.TAR_TREE,
            DatabaseFormat.TAR_GZ_TREE,
            DatabaseFormat.TAR_XZ_TREE
    };

    @Test
    public void test() {
        for (DatabaseFormat format : this.toTest) {
            System.out.println("Testing " + format.name() + "...");
            File file = new File(".", "run/archiveTest_" + format.name()).getAbsoluteFile();
            if (file.exists()) {
                this.deleteDirectory(file);
                file.mkdirs();
            } else {
                file.mkdirs();
            }
            Map<byte[], byte[]> map = new HashMap<>();
            Random random = ThreadLocalRandom.current();
            PorkDB<byte[], byte[]> db = new DBBuilder<byte[], byte[]>()
                    .setMaxOpenFiles(32)
                    .setRootFolder(file)
                    .setKeyHasher(new HashKeyHasher(HashAlg.MD2))
                    .setValueSerializer(ByteArraySerializer.getInstance())
                    .setFormat(format)
                    .build();

            {
                for (int i = 0; i < 1024; i++) {
                    byte[] k = new byte[random.nextInt(40) + 2];
                    random.nextBytes(k);
                    byte[] v = new byte[random.nextInt(8192) + 4096];
                    random.nextBytes(v);
                    map.put(k, v);
                    db.put(k, v);
                }
            }

            {
                map.forEach((k, v) -> {
                    byte[] dbv = db.get(k);
                    if (dbv.length != v.length || !Arrays.equals(dbv, v)) {
                        throw new IllegalStateException();
                    }
                });
            }

            db.forEach((k, v) -> {
            });

            db.shutdown();
        }
    }

    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                this.deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
