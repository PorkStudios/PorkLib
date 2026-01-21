/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.primitive.map.concurrent;

import lombok.AllArgsConstructor;
import net.daporkchop.lib.unsafe.PUnsafe;

import static net.daporkchop.lib.primitive.map.concurrent.ConcurrentHashMapHelper.*;

/**
 * Base implementation of a concurrent hash map implementation.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractConcurrentHashMap<NODE> {
    protected static final long SIZECTL = PUnsafe.pork_getOffset(AbstractConcurrentHashMap.class, "sizeCtl");
    protected static final long TRANSFERINDEX = PUnsafe.pork_getOffset(AbstractConcurrentHashMap.class, "transferIndex");
    protected static final long BASECOUNT = PUnsafe.pork_getOffset(AbstractConcurrentHashMap.class, "baseCount");
    protected static final long CELLSBUSY = PUnsafe.pork_getOffset(AbstractConcurrentHashMap.class, "cellsBusy");
    protected static final long CELLVALUE = PUnsafe.pork_getOffset(CounterCell.class, "value");

    protected transient volatile long baseCount;
    protected transient volatile int sizeCtl;
    protected transient volatile int transferIndex;
    protected transient volatile int cellsBusy;
    protected transient volatile CounterCell[] counterCells;

    protected transient volatile NODE[] table;
    protected transient volatile NODE[] nextTable;

    protected abstract NODE[] allocateTable(int length);

    //implements size() in _P0__P1_Map
    public int size() {
        return (int) Math.max(Math.min(this.sumCount(), Integer.MAX_VALUE), 0L);
    }

    //implements isEmpty() in _P0__P1_Map
    public boolean isEmpty() {
        return this.sumCount() <= 0L;
    }

    protected final long sumCount() {
        CounterCell[] counterCells = this.counterCells;
        long sum = this.baseCount;
        if (counterCells != null) {
            for (CounterCell cell : counterCells) {
                if (cell != null) {
                    sum += cell.value;
                }
            }
        }
        return sum;
    }

    protected void addCount(long x, int check)  {
        CounterCell[] counterCells = this.counterCells;
        long baseCount;
        long sum;
        if (counterCells != null || !PUnsafe.compareAndSwapLong(this, BASECOUNT, baseCount = this.baseCount, sum = baseCount + x))   {
            CounterCell cell;
            boolean uncontended = true;
            if (counterCells == null || counterCells.length == 0
                || (cell = counterCells[getProbe() & (counterCells.length - 1)]) == null
                || !(uncontended = PUnsafe.compareAndSwapLong(cell, CELLVALUE, baseCount = cell.value, baseCount + x))) {
                this.fullAddCount(x, uncontended);
                return;
            } else if (check <= 1)  {
                return;
            }
            sum = this.sumCount();
        }
        if (check >= 0) {
            NODE[] table;
            int sizeCtl;
            while (sum >= (sizeCtl = this.sizeCtl) && (table = this.table) != null && table.length < MAXIMUM_CAPACITY)  {
                int stamp = resizeStamp(table.length);
                if (sizeCtl < 0)    {
                    NODE[] nextTable;
                    //noinspection ConstantConditions
                    if (sizeCtl >>> RESIZE_STAMP_SHIFT != stamp || sizeCtl == stamp + 1 || sizeCtl == stamp + MAX_RESIZERS
                        || (nextTable = this.nextTable) == null || this.transferIndex <= 0)  {
                        break;
                    } else if (PUnsafe.compareAndSwapInt(this, SIZECTL, sizeCtl, sizeCtl + 1))  {
                        this.transfer(table, nextTable);
                    }
                } else if (PUnsafe.compareAndSwapInt(this, SIZECTL, sizeCtl, (stamp << RESIZE_STAMP_SHIFT) + 2))    {
                    this.transfer(table, null);
                }
                sum = this.sumCount();
            }
        }
    }

    protected final void fullAddCount(long x, boolean wasUncontended) {
        int probe = getProbe();
        if (probe == 0) {
            initProbe();
            probe = getProbe();
            wasUncontended = true;
        }
        boolean collision = false;
        while (true) {
            CounterCell[] counterCells = this.counterCells;
            long v;
            if (counterCells != null && counterCells.length > 0) {
                CounterCell cell = counterCells[probe & (counterCells.length - 1)];
                if (cell == null) {
                    if (this.cellsBusy == 0) {
                        CounterCell newCell = new CounterCell(x);
                        if (this.cellsBusy == 0 && PUnsafe.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                            try {
                                CounterCell[] counterCells2 = this.counterCells;
                                if (counterCells2 != null && counterCells2.length > 0) {
                                    int index = probe & (counterCells2.length - 1);
                                    if (counterCells2[index] == null) {
                                        counterCells2[index] = newCell;
                                        break; //CHM uses a "created" flag to do this, but as far as i can tell this is effectively the same
                                    }
                                }
                            } finally {
                                this.cellsBusy = 0;
                            }
                            continue;
                        }
                    }
                    collision = false;
                } else if (!wasUncontended) {
                    wasUncontended = true;
                } else if (PUnsafe.compareAndSwapLong(cell, CELLVALUE, v = cell.value, v + x)) {
                    break;
                } else //noinspection ArrayEquality
                    if (this.counterCells != counterCells || counterCells.length >= NCPU) {
                        collision = false;
                    } else if (!collision) {
                        collision = true;
                    } else if (this.cellsBusy == 0 && PUnsafe.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                        try {
                            //noinspection ArrayEquality
                            if (this.counterCells == counterCells) {
                                CounterCell[] newCounterCells = new CounterCell[counterCells.length << 1];
                                System.arraycopy(counterCells, 0, newCounterCells, 0, counterCells.length);
                                this.counterCells = newCounterCells;
                            }
                        } finally {
                            this.cellsBusy = 0;
                        }
                        collision = false;
                        continue;
                    }
                probe = advanceProbe(probe);
            } else //noinspection ArrayEquality
                if (this.cellsBusy == 0 && this.counterCells == counterCells && PUnsafe.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                    try {
                        //noinspection ArrayEquality
                        if (this.counterCells == counterCells) {
                            counterCells = new CounterCell[2];
                            counterCells[probe & 1] = new CounterCell(x);
                            this.counterCells = counterCells;
                            break; //CHM uses an "init" flag to do this, but as far as i can tell this is effectively the same
                        }
                    } finally {
                        this.cellsBusy = 0;
                    }
                } else if (PUnsafe.compareAndSwapLong(this, BASECOUNT, v = this.baseCount, v + x)) {
                    break;
                }
        }
    }

    protected NODE[] initTable() {
        NODE[] table = this.table;
        for (; table == null || table.length == 0; table = this.table)  {
            int sizeCtl = this.sizeCtl;
            if (sizeCtl < 0) {
                Thread.yield();
            } else if (PUnsafe.compareAndSwapInt(this, SIZECTL, sizeCtl, -1))   {
                try {
                    if ((table = this.table) == null || table.length == 0)  {
                        int capacity = sizeCtl > 0 ? sizeCtl : DEFAULT_CAPACITY;
                        this.table = table = this.allocateTable(capacity);
                        sizeCtl = capacity - (capacity >>> 2);
                    }
                } finally {
                    this.sizeCtl = sizeCtl;
                }
                break;
            }
        }
        return table;
    }

    protected void tryPresize(int size) {
        int capacity = size >= (MAXIMUM_CAPACITY >>> 1) ? MAXIMUM_CAPACITY : tableSizeFor(size + (size >>> 1) + 1);
        int sizeCtl;
        while ((sizeCtl = this.sizeCtl) >= 0) {
            NODE[] table = this.table;
            if (table == null || table.length == 0) {
                int n = Math.max(sizeCtl, capacity);
                if (PUnsafe.compareAndSwapInt(this, SIZECTL, sizeCtl, -1)) {
                    try {
                        //noinspection ArrayEquality
                        if (this.table == table) {
                            this.table = this.allocateTable(n);
                            sizeCtl = n - (n >>> 2);
                        }
                    } finally {
                        this.sizeCtl = sizeCtl;
                    }
                }
            } else if (capacity <= sizeCtl || table.length >= MAXIMUM_CAPACITY) {
                break;
            } else //noinspection ArrayEquality
                if (table == this.table) {
                    int stamp = resizeStamp(table.length);
                    //noinspection ConstantConditions
                    if (sizeCtl < 0) {
                        NODE[] nextTable;
                        if ((sizeCtl >>> RESIZE_STAMP_SHIFT) != stamp || sizeCtl == stamp + 1 || sizeCtl == stamp + MAX_RESIZERS
                            || (nextTable = this.nextTable) == null || this.transferIndex <= 0) {
                            break;
                        } else if (PUnsafe.compareAndSwapInt(this, SIZECTL, sizeCtl, sizeCtl + 1)) {
                            this.transfer(table, nextTable);
                        }
                    } else if (PUnsafe.compareAndSwapInt(this, SIZECTL, sizeCtl, (stamp << RESIZE_STAMP_SHIFT) + 2)) {
                        this.transfer(table, null);
                    }
                }
        }
    }

    protected abstract void transfer(NODE[] table, NODE[] nextTable);

    /**
     * A single stripe containing a counter value.
     *
     * @author DaPorkchop_
     */
    @AllArgsConstructor
    @sun.misc.Contended
    protected static final class CounterCell {
        protected volatile long value;
    }
}
