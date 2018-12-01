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
 */

package net.daporkchop.lib.minecraft.protocol.generator;

import com.google.gson.JsonParser;
import lombok.NonNull;
import net.daporkchop.lib.minecraft.protocol.generator.data.JavaGenerator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A thing that can generate data classes from whatever format of data dump
 *
 * @author DaPorkchop_
 */
public interface DataGenerator {
    Map<String, BiFunction<String, File, DataGenerator>> GENERATORS = new HashMap<String, BiFunction<String, File, DataGenerator>>() {
        {
            this.put("java", JavaGenerator::new);
        }
    };
    File IN_ROOT = new File("./src/main/resources/data/");
    File OUT_ROOT = new File("./../src/main/java/net/daporkchop/lib/minecraft/protocol/mc/");
    JsonParser JSON_PARSER = new JsonParser();

    /**
     * Gets the file that this generator is reading from
     *
     * @return the file that this generator is reading from
     */
    File getInput();

    /**
     * Runs the generator
     *
     * @param out the output file to generate to
     *
     * @throws IOException if an IO exception occurs you dummy
     */
    void run(@NonNull File out) throws IOException;

    default File ensureDirectoryExists(@NonNull File dir) throws IOException {
        if (dir.exists()) {
            if (!dir.isDirectory()) {
                throw new IllegalStateException(String.format("Not a directory: %s", dir.getAbsolutePath()));
            }
        } else if (!dir.mkdirs()) {
            throw new IllegalStateException(String.format("Couldn't create directory: %s", dir.getAbsolutePath()));
        }
        return dir;
    }

    default File ensureFileExists(@NonNull File file) throws IOException {
        if (file.exists()) {
            if (!file.isFile()) {
                throw new IllegalStateException(String.format("Not a file: %s", file.getAbsolutePath()));
            }
        } else {
            this.ensureDirectoryExists(file.getParentFile());
            if (!file.createNewFile()) {
                throw new IllegalStateException(String.format("Couldn't create file: %s", file.getAbsolutePath()));
            }
        }
        return file;
    }
}
