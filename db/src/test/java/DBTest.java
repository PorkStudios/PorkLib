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
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.db.container.map.data.OneTimeWriteDataLookup;
import net.daporkchop.lib.binary.data.impl.BasicSerializer;
import net.daporkchop.lib.encoding.basen.Base58;
import net.daporkchop.lib.encoding.compression.Compression;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.abs;

/**
 * @author DaPorkchop_
 */
public class DBTest {
    private static final File outFile = new File(".", "test_out/db");

    static {
        PorkUtil.rm(outFile);
    }

    /*@Test
    public void testIndexLookup() throws IOException {
        Map<String, Long> dataMap = new Hashtable<>();
        {
            Random r = ThreadLocalRandom.current();
            for (int i = 0; i < 5; i++) {
                byte[] b = new byte[16];
                r.nextBytes(b);
                dataMap.put(Base58.encodeBase58(b), abs(r.nextLong()));
            }
        }
        {
            IndexLookup<String> lookup;// = new SlowAndInefficientTreeIndexLookup<>();
            PorkDB db = PorkDB.builder()
                    .setRoot(outFile)
                    .build();
            {
                DBMap<String, Object> map = DBMap.<String, Object>builder(db, "jeff")
                        .setValueSerializer(new Serializer<Object>() {
                            @Override
                            public void write(Object val, DataOut out) {
                            }

                            @Override
                            public Object read(DataIn in) {
                                return null;
                            }
                        }).build();
                lookup = map.getIndexLookup();
            }
            dataMap.forEach((key, data) -> {
                try {
                    lookup.set(key, data);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            //lookup.save();
            db.close();
        }
        System.out.println("AAAAAA");
        {
            IndexLookup<String> lookup;// = new SlowAndInefficientTreeIndexLookup<>();
            PorkDB db = PorkDB.builder()
                    .setRoot(outFile)
                    .build();
            {
                DBMap<String, Object> map = DBMap.<String, Object>builder(db, "jeff")
                        .setValueSerializer(new Serializer<Object>() {
                            @Override
                            public void write(Object val, DataOut out) {
                            }

                            @Override
                            public Object read(DataIn in) {
                                return null;
                            }
                        }).build();
                lookup = map.getIndexLookup();
            }
            dataMap.forEach((key, data) -> {
                try {
                    if (lookup.get(key) != data) {
                        throw new IllegalStateException(String.format("data for key %s is not the same on disk (%d != %d)", key, data, lookup.get(key)));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            db.close();
        }
    }*/

    @Test
    @SuppressWarnings("unchecked")
    public void test() throws IOException {
        Random r = ThreadLocalRandom.current();
        long longVal = r.nextLong();
        Map<String, byte[]> data = new Hashtable<>();
        for (int i = 0; i < 15; i++) {
            byte[] b1 = new byte[16];
            byte[] b2 = new byte[1024];
            r.nextBytes(b1);
            r.nextBytes(b2);
            data.put(Base58.encodeBase58(b1), b2);
        }
        {
            byte[] b1 = new byte[16];
            byte[] b2 = new byte[0xFFFFFF];
            r.nextBytes(b1);
            data.put(Base58.encodeBase58(b1), b2);
        }
        {
            PorkDB db = PorkDB.builder()
                    .setRoot(outFile)
                    .build();

            if (false){
                DBAtomicLong atomicLong = DBAtomicLong.builder(db, "long").build();
                atomicLong.set(longVal);
            }
            if (true)   {
                DBMap<String, byte[]> dbMap = DBMap.<String, byte[]>builder(db, "map")
                        .setValueSerializer(new BasicSerializer<>())
                        .setDataLookup(new OneTimeWriteDataLookup())
                        .setCompression(Compression.BZIP2_NORMAL)
                        .build();

                dbMap.putAll(data);
            }
            db.close();
        }
        {
            PorkDB db = PorkDB.builder()
                    .setRoot(outFile)
                    .build();

            try {
                if (false){
                    DBAtomicLong atomicLong = DBAtomicLong.builder(db, "long").buildIfPresent();
                    Objects.requireNonNull(atomicLong);
                    if (atomicLong.get() != longVal) {
                        throw new IllegalStateException(String.format("Inconsistent values: real=%d, on disk=%d", longVal, atomicLong.get()));
                    }
                }
                if (true)   {
                    DBMap<String, byte[]> dbMap = DBMap.<String, byte[]>builder(db, "map")
                            .setValueSerializer(new BasicSerializer<>())
                            .setDataLookup(new OneTimeWriteDataLookup())
                            .setCompression(Compression.BZIP2_NORMAL)
                            .build();

                    data.forEach((key, val) -> {
                        if (!dbMap.containsKey(key))    {
                            throw new IllegalStateException(String.format("Missing key: %s", key));
                        }
                        byte[] diskVal = dbMap.get(key);
                        if (!Arrays.equals(val, diskVal))   {
                            throw new IllegalStateException(String.format("Value for key %s is incorrect!", key));
                        }
                    });
                }
            } finally {
                db.close();
            }
        }
    }
}
