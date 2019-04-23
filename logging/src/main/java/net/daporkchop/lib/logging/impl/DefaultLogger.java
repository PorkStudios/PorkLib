package net.daporkchop.lib.logging.impl;

/**
 * @author DaPorkchop_
 */
public class DefaultLogger extends BaseLogger {
    public DefaultLogger()  {
        super(System.out::println);
    }
}
