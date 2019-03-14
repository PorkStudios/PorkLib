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

package net.daporkchop.lib.dbextensions.leveldb;

import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.binary.serialization.Serializer;
import net.daporkchop.lib.common.function.io.IOBiFunction;
import net.daporkchop.lib.common.setting.Option;
import net.daporkchop.lib.common.setting.OptionGroup;
import net.daporkchop.lib.common.util.PUnsafe;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.hash.util.Digest;
import net.daporkchop.lib.logging.Logger;
import org.iq80.leveldb.CompressionType;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
public interface OptionsLevelDB {
    Option<File> PATH = Option.required("PATH");
    Option<Boolean> CREATE_IF_MISSING = Option.of("CREATE_IF_MISSING", true);
    Option<Boolean> ERROR_IF_EXISTS = Option.of("ERROR_IF_EXISTS", false);
    Option<Integer> WRITE_BUFFER_SIZE = Option.of("WRITE_BUFFER_SIZE", 4 << 20);
    Option<Integer> MAX_OPEN_FILES = Option.of("MAX_OPEN_FILES", 1000);
    Option<Integer> BLOCK_RESTART_INTERVAL = Option.of("BLOCK_RESTART_INTERVAL", 16);
    Option<Integer> BLOCK_SIZE = Option.of("BLOCK_SIZE", 4 * 1024);
    Option<CompressionType> COMPRESSION_TYPE = Option.of("COMPRESSION_TYPE", CompressionType.SNAPPY);
    Option<Boolean> VERIFY_CHECKSUMS = Option.of("VERIFY_CHECKSUMS", true);
    Option<Boolean> PARANIOD_CHECKS = Option.of("PARANIOD_CHECKS", false);
    Option<Consumer<String>> LOGGER = Option.of("LOGGER", Logger.DEFAULT_LOG::info);
    Option<Long> CACHE_SIZE = Option.of("CACHE_SIZE", 0L);
    Option<IOBiFunction<Integer, String, byte[]>> PREFIX_GENERATOR = Option.of("PREFIX_GENERATOR", (type, name) -> {
        byte[] b = new byte[8];
        b[0] = type.byteValue();
        System.arraycopy(Digest.WHIRLPOOL.hash(name.getBytes(UTF8.utf8), b).getHash(), 0, b, 1, 7);
        return b;
    });

    OptionGroup LEVELDB_INIT_OPTIONS = OptionGroup.of(
            PATH,

            CREATE_IF_MISSING,
            ERROR_IF_EXISTS,
            WRITE_BUFFER_SIZE,
            MAX_OPEN_FILES,
            BLOCK_RESTART_INTERVAL,
            BLOCK_SIZE,
            COMPRESSION_TYPE,
            VERIFY_CHECKSUMS,
            PARANIOD_CHECKS,
            LOGGER,
            CACHE_SIZE,

            PREFIX_GENERATOR
    );

    Option<byte[]> CONTAINER_PREFIX = Option.optional("CONTAINER_PREFIX");

    Option<Serializer> MAP_FAST_KEY_SERIALIZER = Option.optional("FAST_KEY_SERIALIZER");

    OptionGroup LEVELDB_MAP_OPTIONS = OptionGroup.of(
            DBMap.DB_MAP_OPTIONS,
            CONTAINER_PREFIX,
            MAP_FAST_KEY_SERIALIZER
    );
}
