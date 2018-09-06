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

package net.daporkchop.lib.math.arrays;

import lombok.NonNull;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
public class ShuffleArray {
    public static void shuffleB(@NonNull byte[] arr) {
        shuffleB(arr, ThreadLocalRandom.current());
    }

    public static void shuffleB(@NonNull byte[] arr, @NonNull Random random) {
        for (int i = 0; i < arr.length; i++) {
            int j = random.nextInt(arr.length);
            byte curr = arr[i];
            arr[i] = arr[j];
            arr[j] = curr;
        }
    }

    public static void shuffleS(@NonNull short[] arr) {
        shuffleS(arr, ThreadLocalRandom.current());
    }

    public static void shuffleS(@NonNull short[] arr, @NonNull Random random) {
        for (int i = 0; i < arr.length; i++) {
            int j = random.nextInt(arr.length);
            short curr = arr[i];
            arr[i] = arr[j];
            arr[j] = curr;
        }
    }

    public static void shuffleI(@NonNull int[] arr) {
        shuffleI(arr, ThreadLocalRandom.current());
    }

    public static void shuffleI(@NonNull int[] arr, @NonNull Random random) {
        for (int i = 0; i < arr.length; i++) {
            int j = random.nextInt(arr.length);
            int curr = arr[i];
            arr[i] = arr[j];
            arr[j] = curr;
        }
    }

    public static void shuffle(@NonNull Object[] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    public static void shuffle(@NonNull Object[] arr, @NonNull Random random) {
        for (int i = 0; i < arr.length; i++) {
            int j = random.nextInt(arr.length);
            Object curr = arr[i];
            arr[i] = arr[j];
            arr[j] = curr;
        }
    }
}
