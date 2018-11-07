package net.daporkchop.lib.common.util;

import sun.misc.Unsafe;

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
}
