package net.daporkchop.lib.common.util;

import lombok.NonNull;
import sun.misc.Unsafe;

import java.io.File;
import java.lang.reflect.Field;

/**
 * @author DaPorkchop_
 */
public class PorkUtil {
    public static final Unsafe unsafe;

    static {
        Unsafe u = null;
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            u = (Unsafe) f.get(null);
        } catch (Exception e)   {
            throw new RuntimeException(e);
        } finally {
            unsafe = u;
        }
    }

    public static void rm(@NonNull File file)   {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files == null) {
                    throw new NullPointerException(file.getAbsolutePath());
                }
                for (File f : files) {
                    rm(f);
                }
            }
            if (!file.delete()) {
                throw new IllegalStateException(String.format("Could not delete file: %s", file.getAbsolutePath()));
            }
        }
    }
}
