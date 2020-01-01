/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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
import net.daporkchop.lib.collections.PSet;
import net.daporkchop.lib.collections.impl.ordered.BigLinkedCollection;
import net.daporkchop.lib.collections.impl.set.JavaSetWrapper;
import net.daporkchop.lib.common.misc.TestRandomData;
import net.daporkchop.lib.db.DBSet;
import net.daporkchop.lib.encoding.basen.Base58;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static net.daporkchop.lib.logging.Logging.*;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class DBSetTest implements TestConstants {
    public static final int START_COUNT = 512;

    @NonNull
    protected final Supplier<DBSet<String>> setSupplier;

    public void test() throws IOException {
        logger.info("Testing %s set...", NAME.get());

        PSet<String> java = new JavaSetWrapper<>(new HashSet<>());
        for (int i = START_COUNT - 1; i >= 0; i--) {
            java.add(Base58.encodeBase58(TestRandomData.getRandomBytes(16)));
        }

        try (DBSet<String> db = this.setSupplier.get()) {
            logger.debug("Adding values...");
            java.forEach(db::add);
            logger.debug("All values added.");
        }

        try (DBSet<String> db = this.setSupplier.get()) {
            this.checkIdentical(java, db);
            logger.debug("Deleting some random values from memory...");

            PCollection<String> toRemove = java.stream()
                                               .filter(v -> ThreadLocalRandom.current().nextBoolean())
                                               .collect(BigLinkedCollection::new);
            toRemove.forEach(java::remove);
            toRemove.forEach(db::remove);
            logger.debug("Set now contains %d/%d values", java.size(), START_COUNT);
        }

        try (DBSet<String> db = this.setSupplier.get()) {
            this.checkIdentical(java, db);
        }

        logger.success("Tested %s set!", NAME.get());
    }

    protected void checkIdentical(@NonNull PSet<String> java, @NonNull PSet<String> db) {
        logger.debug("Ensuring database contains all values in memory...");
        java.forEach(value -> {
            if (!db.contains(value)) {
                throw new IllegalStateException(String.format("Database does not contain value: %s", value));
            }
        });
        logger.debug("Ensuring database contains no values not present in memory...");
        db.forEach(value -> {
            if (!java.contains(value)) {
                throw new IllegalStateException(String.format("Database contains non-existent value: %s", value));
            }
        });
    }
}
