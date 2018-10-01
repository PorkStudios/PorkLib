package net.daporkchop.lib.network.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ToggleableLock {
    private volatile boolean locked = false;

    public void test()  {
        while (this.locked) {
            try {
                synchronized (this) {
                    this.wait(50L);
                }
            } catch (InterruptedException e)    {
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void toggle()    {
        if (!(this.locked = !this.locked)) {
            synchronized (this) {
                this.notifyAll();
            }
        }
    }
}
