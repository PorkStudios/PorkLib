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

import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.db.container.DBAtomicLong;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
public class DBTest {
    private static final File outFile = new File(".", "test_out/db");

    static {
        PorkUtil.rm(outFile);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test() throws IOException {
        Random r = ThreadLocalRandom.current();
        long longVal = r.nextLong();
        {
            PorkDB db = PorkDB.builder()
                    .setRoot(outFile)
                    .build();

            {
                DBAtomicLong atomicLong = DBAtomicLong.builder(db, "long").build();
                atomicLong.set(longVal);
            }
            db.close();
        }
        {
            PorkDB db = PorkDB.builder()
                    .setRoot(outFile)
                    .build();

            try {
                {
                    DBAtomicLong atomicLong = DBAtomicLong.builder(db, "long").buildIfPresent();
                    Objects.requireNonNull(atomicLong);
                    if (atomicLong.get() != longVal) {
                        throw new IllegalStateException(String.format("Inconsistent values: real=%d, on disk=%d", longVal, atomicLong.get()));
                    }
                }
            } finally {
                db.close();
            }
        }
    }
}
