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

package db;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.collections.PCollection;
import net.daporkchop.lib.collections.PMap;
import net.daporkchop.lib.collections.impl.map.JavaMapWrapper;
import net.daporkchop.lib.collections.impl.ordered.BigLinkedCollection;
import net.daporkchop.lib.common.misc.TestRandomData;
import net.daporkchop.lib.db.DBMap;
import net.daporkchop.lib.encoding.Hexadecimal;
import net.daporkchop.lib.encoding.basen.Base58;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class DBMapTest implements TestConstants {
    public static final int START_COUNT = 512;
    public static final int DATA_MIN = 64;
    public static final int DATA_MAX = 8192;

    @NonNull
    protected final Supplier<DBMap<String, byte[]>> mapSupplier;

    public void test() throws IOException {
        logger.info("Testing %s map...", NAME.get());

        PMap<String, byte[]> java = new JavaMapWrapper<>(new HashMap<>());
        for (int i = START_COUNT - 1; i >= 0; i--)  {
            java.put(Base58.encodeBase58(TestRandomData.getRandomBytes(16)), TestRandomData.getRandomBytes(DATA_MIN, DATA_MAX));
        }

        try (DBMap<String, byte[]> db = this.mapSupplier.get()) {
            logger.debug("Putting values...");
            java.forEach(db::put);
            logger.debug("All values put.");
        }

        try (DBMap<String, byte[]> db = this.mapSupplier.get()) {
            this.checkIdentical(java, db);
            logger.debug("Deleting some random entries from memory...");

            PCollection<String> toRemove = java.keyStream()
                    .filter(v -> ThreadLocalRandom.current().nextBoolean())
                    .collect(BigLinkedCollection::new);
            toRemove.forEach(java::remove);
            toRemove.forEach(db::remove);
            logger.debug("Map now contains %d/%d entries", java.size(), START_COUNT);
        }

        try (DBMap<String, byte[]> db = this.mapSupplier.get()) {
            this.checkIdentical(java, db);
            db.valueStream()
              .map(Hexadecimal::encode)
              .filter(s -> s.startsWith("0"))
              .forEach(logger::trace);
        }

        logger.success("Tested %s map!", NAME.get());
    }

    protected void checkIdentical(@NonNull PMap<String, byte[]> java, @NonNull PMap<String, byte[]> db) {
        logger.debug("Ensuring database contains all values in memory...");
        java.forEach((key, value) -> {
            if (!db.contains(key))  {
                throw new IllegalStateException(String.format("Database does not contain key: %s", key));
            }
            byte[] dbValue = db.get(key);
            if (!Arrays.equals(value, dbValue)) {
                throw new IllegalStateException(String.format("Database contains invalid value for key: %s", key));
            }
        });
        logger.debug("Ensuring database contains no values not present in memory...");
        db.forEach((key, value) -> {
            AtomicBoolean found = new AtomicBoolean(false);
            java.forEachValue(val -> {
                if (Arrays.equals(value, val) && found.getAndSet(true))  {
                    throw new IllegalStateException("Duplicate value!");
                }
            });
            if (!found.get())  {
                throw new IllegalStateException(String.format("Memory does not contain key: %s", key));
            }
        });
    }
}
