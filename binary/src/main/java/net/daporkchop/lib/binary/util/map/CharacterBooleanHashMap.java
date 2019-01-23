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

package net.daporkchop.lib.binary.util.map;

import net.daporkchop.lib.math.primitive.BinMath;

import java.util.BitSet;

/**
 * A hash map, using a key type of char and testMethodThing value type of boolean.
 * This works™, but isn't particularly efficiently
 * At some point I might get around to fixing this, until then use tree maps as they work correctly
 * <p>
 * DO NOT EDIT BY HAND! THIS FILE IS SCRIPT-GENERATED!
 *
 * @author DaPorkchop_
 */
public class CharacterBooleanHashMap implements CharacterBooleanMap {
    protected final int baseSize;
    protected final CharacterToIntegerFunction keyHash;
    protected char[] keys;
    protected boolean[] values;
    protected BitSet states;
    protected int len;
    protected int shrinkThreshold;
    protected int growThreshold;
    protected int mask;
    protected int size;

    public CharacterBooleanHashMap() {
        this(16384);
    }

    public CharacterBooleanHashMap(int baseSize) {
        this(baseSize, null);
    }

    public CharacterBooleanHashMap(CharacterToIntegerFunction keyHash) {
        this(16384, keyHash);
    }

    public CharacterBooleanHashMap(int baseSize, CharacterToIntegerFunction keyHash) {
        if (!BinMath.isPow2(baseSize)) throw new IllegalArgumentException("baseSize must be a power of 2!");
        this.baseSize = baseSize;
        if (keyHash == null) {
            this.keyHash = in -> in & 0xFFFF;
        } else {
            this.keyHash = keyHash;
        }

        //clear function sets up the arrays and such for us
        this.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean get(char key) {
        int i = this.getIndex(key);
        //check if key is present
        if (this.states.get(i)) {
            return this.values[i];
        } else {
            //if not, return empty value
            return false;
        }
    }

    @Override
    public boolean put(char key, boolean value) {
        int i = this.getIndex(key);
        //check if key is present
        if (this.states.get(i)) {
            //if the new key isn't equal to the stored key, we've got us a hash collision
            //if that happens, just expand the array over and over until there's no more collisions
            if (key != this.keys[i]) {
                this.grow(true);
                //fetch new index, as the arrays have been changed
                i = this.getIndex(key);
                //increment size index because this is a new entry
                this.size++;
            }
            //fetch and overwrite old value, then return it
            @SuppressWarnings("unchecked")
            boolean old = this.values[i];
            this.values[i] = value;
            return old;
        } else {
            //mark key index as full
            this.states.set(i);
            //actually set data
            this.keys[i] = key;
            this.values[i] = value;
            //increment size and check if we need to grow the backing arrays
            this.size++;
            this.grow(false);
            //return empty value, as there was no old impl to return
            return false;
        }
    }

    public void clear() {
        this.keys = new char[this.baseSize];
        this.values = new boolean[this.baseSize];
        this.states = new BitSet();

        this.len = this.baseSize;
        this.updateConstants();
        this.size = 0;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    protected boolean forEachBreaking(IntegerToBooleanFunction function) {
        for (int i = this.states.nextSetBit(0); i != -1; i = this.states.nextSetBit(i + 1)) {
            if (function.apply(i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void forEachKey(CharacterConsumer consumer) {
        for (int i = this.states.nextSetBit(0); i != -1; i = this.states.nextSetBit(i + 1)) {
            consumer.accept(this.keys[i]);
        }
    }

    protected void grow(boolean force) {
        if (force || this.size >= this.growThreshold) {
            //System.out.println("Growing from current size: " + len);
            boolean[] values;
            char[] keys;
            BitSet states;
            //repeat until there's no hash collisions
            do {
                //multiply length by 2 and update local values accordingly
                this.len <<= 1;
                this.updateConstants();
                //init new arrays
                values = new boolean[this.len];
                keys = new char[this.len];
                states = new BitSet();
            } while (this.reHash(values, keys, states));
            this.values = values;
            this.keys = keys;
            this.states = states;
            //System.out.println("Grew! New size: " + len);
        }
    }

    protected boolean reHash(boolean[] values, char[] keys, BitSet states) {
        return this.forEachBreaking(i -> {
            char key = this.keys[i];
            int j = this.getIndex(key);
            if (states.get(j)) {
                //there's already an element with this index!
                return true;
            }
            states.set(j);
            values[j] = this.values[i];
            keys[j] = key;
            return false;
        });
    }

    protected int getIndex(char key) {
        return this.keyHash.apply(key) & this.mask;
    }

    protected void updateConstants() {
        this.shrinkThreshold = this.len >> 1;
        this.growThreshold = this.len << 1;
        this.mask = this.len - 1;
    }

    protected boolean removeIndex(int i) {
        //check if the key is present
        if (this.states.get(i)) {
            //mark the index as deleted
            //key and value arrays don't need to be reset, as they'll be overwritten if a key with the same index is added later
            this.states.clear(i);
            //decrement size
            this.size--;
            //the value is actually still in the array, just there's no indexes pointing to it anymore after this call
            return this.values[i];
            //that'll be a PITA in case there's testMethodThing hash collision during shrinking because
            //it'll then proceed to keep trying to shrink down the array and having collisions
            //until one of the colliding elements is removed.
            //caching the indexes of the colliding elements is an option, but is probably useless overhead
        } else {
            //if not, return empty value
            return false;
        }
    }
}
