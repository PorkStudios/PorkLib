import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.db.container.impl.DBAtomicLong;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
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
        /*Random r = ThreadLocalRandom.current();
        long longVal = r.nextLong();
        {
            PorkDB db = PorkDB.builder()
                    .setRoot(outFile)
                    .build();

            {
                DBAtomicLong atomicLong = db.loadAtomicLong("long");
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
                    DBAtomicLong atomicLong = db.loadAtomicLong("long");
                    if (atomicLong.get() != longVal) {
                        throw new IllegalStateException(String.format("Inconsistent values: real=%d, on disk=%d", longVal, atomicLong.get()));
                    }
                }
            } finally {
                db.close();
            }
        }*/
        System.out.println(DBAtomicLong.Builder.class.getCanonicalName());
    }
}
