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

package net.daporkchop.lib.common.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;

/**
 * Wrapper class around {@link Unsafe}.
 * 
 * Really serves very little purpose except to avoid the otherwise unavoidable "Internal API" warnings at compile-time
 * caused by referencing anything in {@link sun}
 * 
 * @author DaPorkchop_
 */
public interface PUnsafe {
    /**
     * A constant reference to {@link Unsafe}
     */
    Unsafe UNSAFE = PorkUtil.unsafe;

    static int getInt(Object var1, long var2)   {
        return UNSAFE.getInt(var1, var2);
    }

    static void putInt(Object var1, long var2, int var4)    {
        UNSAFE.putInt(var1, var2, var4);
    }

    @SuppressWarnings("unchecked")
    static <T> T getObject(Object var1, long var2) {
        return (T) UNSAFE.getObject(var1, var2);
    }

    static void putObject(Object var1, long var2, Object var4)  {
        UNSAFE.putObject(var1, var2, var4);
    }

    static boolean getBoolean(Object var1, long var2)   {
        return UNSAFE.getBoolean(var1, var2);
    }

    static void putBoolean(Object var1, long var2, boolean var4)    {
        UNSAFE.putBoolean(var1, var2, var4);
    }

    static byte getByte(Object var1, long var2) {
        return UNSAFE.getByte(var1, var2);
    }

    static void putByte(Object var1, long var2, byte var4)  {
        UNSAFE.putByte(var1, var2, var4);
    }

    static short getShort(Object var1, long var2)   {
        return UNSAFE.getShort(var1, var2);
    }

    static void putShort(Object var1, long var2, short var4)    {
        UNSAFE.putShort(var1, var2, var4);
    }

    static char getChar(Object var1, long var2) {
        return UNSAFE.getChar(var1, var2);
    }

    static void putChar(Object var1, long var2, char var4)  {
        UNSAFE.putChar(var1, var2, var4);
    }

    static long getLong(Object var1, long var2) {
        return UNSAFE.getLong(var1, var2);
    }

    static void putLong(Object var1, long var2, long var4)  {
        UNSAFE.putLong(var1, var2, var4);
    }

    static float getFloat(Object var1, long var2)   {
        return UNSAFE.getFloat(var1, var2);
    }

    static void putFloat(Object var1, long var2, float var4)    {
        UNSAFE.putFloat(var1, var2, var4);
    }

    static double getDouble(Object var1, long var2) {
        return UNSAFE.getDouble(var1, var2);
    }

    static void putDouble(Object var1, long var2, double var4)  {
        UNSAFE.putDouble(var1, var2, var4);
    }

    static int getInt(long var2)   {
        return UNSAFE.getInt(var2);
    }

    static void putInt(long var2, int var4)    {
        UNSAFE.putInt(var2, var4);
    }

    static byte getByte(long var2) {
        return UNSAFE.getByte(var2);
    }

    static void putByte(long var2, byte var4)  {
        UNSAFE.putByte(var2, var4);
    }

    static short getShort(long var2)   {
        return UNSAFE.getShort(var2);
    }

    static void putShort(long var2, short var4)    {
        UNSAFE.putShort(var2, var4);
    }

    static char getChar(long var2) {
        return UNSAFE.getChar(var2);
    }

    static void putChar(long var2, char var4)  {
        UNSAFE.putChar(var2, var4);
    }

    static long getLong(long var2) {
        return UNSAFE.getLong(var2);
    }

    static void putLong(long var2, long var4)  {
        UNSAFE.putLong(var2, var4);
    }

    static float getFloat(long var2)   {
        return UNSAFE.getFloat(var2);
    }

    static void putFloat(long var2, float var4)    {
        UNSAFE.putFloat(var2, var4);
    }

    static double getDouble(long var2) {
        return UNSAFE.getDouble(var2);
    }

    static void putDouble(long var2, double var4)  {
        UNSAFE.putDouble(var2, var4);
    }

    static long getAddress(long var1)   {
        return UNSAFE.getAddress(var1);
    }

    static void putAddress(long var1, long var3)    {
        UNSAFE.putAddress(var1, var3);
    }

    static long allocateMemory(long var1)   {
        return UNSAFE.allocateMemory(var1);
    }

    static long reallocateMemory(long var1, long var3)  {
        return UNSAFE.reallocateMemory(var1, var3);
    }

    static void setMemory(Object var1, long var2, long var4, byte var6) {
        UNSAFE.setMemory(var1, var2, var4, var6);
    }

    static void setMemory(long var1, long var3, byte var5) {
        UNSAFE.setMemory(null, var1, var3, var5);
    }

    static void copyMemory(Object var1, long var2, Object var4, long var5, long var7)   {
        UNSAFE.copyMemory(var1, var2, var4, var5, var7);
    }

    static void copyMemory(long var1, long var3, long var5) {
        UNSAFE.copyMemory(null, var1, null, var3, var5);
    }

    static void freeMemory(long var1)   {
        UNSAFE.freeMemory(var1);
    }

    static long staticFieldOffset(Field var1)   {
        return UNSAFE.staticFieldOffset(var1);
    }

    static long objectFieldOffset(Field var1)   {
        return UNSAFE.objectFieldOffset(var1);
    }

    static Object staticFieldBase(Field var1)   {
        return UNSAFE.staticFieldBase(var1);
    }

    static boolean shouldBeInitialized(Class<?> var1)   {
        return UNSAFE.shouldBeInitialized(var1);
    }

    static void ensureClassInitialized(Class<?> var1)   {
        UNSAFE.ensureClassInitialized(var1);
    }

    static int arrayBaseOffset(Class<?> var1)   {
        return UNSAFE.arrayBaseOffset(var1);
    }

    static int arrayIndexScale(Class<?> var1)   {
        return UNSAFE.arrayIndexScale(var1);
    }

    static int addressSize()     {
        return UNSAFE.addressSize();
    }

    static int pageSize()   {
        return UNSAFE.pageSize();
    }

    static Class<?> defineClass(String var1, byte[] var2, int var3, int var4, ClassLoader var5, ProtectionDomain var6)  {
        return UNSAFE.defineClass(var1, var2, var3, var4, var5, var6);
    }

    static Class<?> defineAnonymousClass(Class<?> var1, byte[] var2, Object[] var3) {
        return UNSAFE.defineAnonymousClass(var1, var2, var3);
    }

    static Object allocateInstance(Class<?> var1) throws InstantiationException {
        return UNSAFE.allocateInstance(var1);
    }

    static void throwException(Throwable var1)  {
        UNSAFE.throwException(var1);
    }

    static boolean compareAndSwapObject(Object var1, long var2, Object var4, Object var5)   {
        return UNSAFE.compareAndSwapObject(var1, var2, var4, var5);
    }

    static boolean compareAndSwapInt(Object var1, long var2, int var4, int var5)    {
        return UNSAFE.compareAndSwapInt(var1, var2, var4, var5);
    }

    static boolean compareAndSwapLong(Object var1, long var2, long var4, long var6) {
        return UNSAFE.compareAndSwapLong(var1, var2, var4, var6);
    }

    @SuppressWarnings("unchecked")
    static <T> T getObjectVolatile(Object var1, long var2) {
        return (T) UNSAFE.getObjectVolatile(var1, var2);
    }

    static void putObjectVolatile(Object var1, long var2, Object var4)  {
        UNSAFE.putObjectVolatile(var1, var2, var4);
    }

    static int getIntVolatile(Object var1, long var2)   {
        return UNSAFE.getIntVolatile(var1, var2);
    }

    static void putIntVolatile(Object var1, long var2, int var4)    {
        UNSAFE.putIntVolatile(var1, var2, var4);
    }

    static boolean getBooleanVolatile(Object var1, long var2)   {
        return UNSAFE.getBooleanVolatile(var1, var2);
    }

    static void putBooleanVolatile(Object var1, long var2, boolean var4)    {
        UNSAFE.putBooleanVolatile(var1, var2, var4);
    }

    static byte getByteVolatile(Object var1, long var2) {
        return UNSAFE.getByteVolatile(var1, var2);
    }

    static void putByteVolatile(Object var1, long var2, byte var4)  {
        UNSAFE.putByteVolatile(var1, var2, var4);
    }

    static short getShortVolatile(Object var1, long var2)   {
        return UNSAFE.getShortVolatile(var1, var2);
    }

    static void putShortVolatile(Object var1, long var2, short var4)    {
        UNSAFE.putShortVolatile(var1, var2, var4);
    }

    static char getCharVolatile(Object var1, long var2) {
        return UNSAFE.getCharVolatile(var1, var2);
    }

    static void putCharVolatile(Object var1, long var2, char var4)  {
        UNSAFE.putCharVolatile(var1, var2, var4);
    }

    static long getLongVolatile(Object var1, long var2) {
        return UNSAFE.getLongVolatile(var1, var2);
    }

    static void putLongVolatile(Object var1, long var2, long var4)  {
        UNSAFE.putLongVolatile(var1, var2, var4);
    }

    static float getFloatVolatile(Object var1, long var2)   {
        return UNSAFE.getFloatVolatile(var1, var2);
    }

    static void putFloatVolatile(Object var1, long var2, float var4)    {
        UNSAFE.putFloatVolatile(var1, var2, var4);
    }

    static double getDoubleVolatile(Object var1, long var2) {
        return UNSAFE.getDoubleVolatile(var1, var2);
    }

    static void putDoubleVolatile(Object var1, long var2, double var4)  {
        UNSAFE.putDoubleVolatile(var1, var2, var4);
    }

    static void putOrderedObject(Object var1, long var2, Object var4)   {
        UNSAFE.putOrderedObject(var1, var2, var4);
    }

    static void putOrderedInt(Object var1, long var2, int var4) {
        UNSAFE.putOrderedInt(var1, var2, var4);
    }

    static void putOrderedLong(Object var1, long var2, long var4)   {
        UNSAFE.putOrderedLong(var1, var2, var4);
    }

    static void unpark(Object var1) {
        UNSAFE.unpark(var1);
    }

    static void park(boolean var1, long var2)   {
        UNSAFE.park(var1, var2);
    }

    static int getLoadAverage(double[] var1, int var2)  {
        return UNSAFE.getLoadAverage(var1, var2);
    }

    static int getAndAddInt(Object var1, long var2, int var4) {
        return UNSAFE.getAndAddInt(var1, var2, var4);
    }

    static long getAndAddLong(Object var1, long var2, long var4) {
        return UNSAFE.getAndAddLong(var1, var2, var4);
    }

    static int getAndSetInt(Object var1, long var2, int var4) {
        return UNSAFE.getAndSetInt(var1, var2, var4);
    }

    static long getAndSetLong(Object var1, long var2, long var4) {
        return UNSAFE.getAndSetLong(var1, var2, var4);
    }

    @SuppressWarnings("unchecked")
    static <T> T getAndSetObject(Object var1, long var2, Object var4) {
        return (T) UNSAFE.getAndSetObject(var1, var2, var4);
    }

    static void loadFence() {
        UNSAFE.loadFence();
    }

    static void storeFence()    {
        UNSAFE.storeFence();
    }

    static void fullFence() {
        UNSAFE.fullFence();
    }
}
