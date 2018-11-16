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

package net.daporkchop.lib.encoding.compression;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.function.IOBiFunction;
import net.daporkchop.lib.common.function.IOFunction;
import net.daporkchop.lib.common.function.ThrowingSupplier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Utility to store initialization functions for various compression algorithms
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompressionHelper<ParamType> {
    static final Map<String, CompressionHelper> nameLookup = Collections.synchronizedMap(new Hashtable<>());

    @NonNull
    @Getter
    private final String name;
    @NonNull
    @Getter
    private final String level;
    private final ParamType params;
    @NonNull
    private final IOBiFunction<InputStream, ParamType, InputStream> inputStreamWrapper;
    @NonNull
    private final IOBiFunction<OutputStream, ParamType, OutputStream> outputStreamWrapper;

    static boolean registerCompressionType(@NonNull String name, @NonNull CompressionHelper helper) {
        return nameLookup.putIfAbsent(name, helper) == null;
    }

    public static CompressionHelper forName(@NonNull String name) {
        return nameLookup.get(name);
    }

    public static void forAllRegisteredAlgs(@NonNull BiConsumer<String, CompressionHelper> consumer) {
        forAllRegisteredAlgs(consumer, false);
    }

    public static void forAllRegisteredAlgs(@NonNull BiConsumer<String, CompressionHelper> consumer, boolean parallel) {
        Stream<Map.Entry<String, CompressionHelper>> stream = nameLookup.entrySet().stream();
        if (parallel) {
            stream = stream.parallel();
        } else {
            stream = stream.sorted(Comparator.comparing(Map.Entry::getKey, Comparator.naturalOrder()));
        }
        stream.forEachOrdered(e -> consumer.accept(e.getKey(), e.getValue()));
    }

    public static <ParamType> Builder<ParamType> builder(@NonNull String name) {
        return new Builder<>(name);
    }

    public static <ParamType> Builder<ParamType> builder(@NonNull String name, @NonNull String level) {
        return new Builder<>(name, level);
    }

    public InputStream inflate(@NonNull InputStream in) throws IOException {
        return this.inputStreamWrapper.applyThrowing(in, this.params);
    }

    public OutputStream deflate(@NonNull OutputStream out) throws IOException {
        return this.outputStreamWrapper.applyThrowing(out, this.params);
    }

    public byte[] inflate(@NonNull byte[] compressed) {
        try (InputStream in = this.inflate(new ByteArrayInputStream(compressed))) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int i;
            while ((i = in.read()) != -1) {
                baos.write(i);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] deflate(@NonNull byte[] original) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream in = new ByteArrayInputStream(original);
             OutputStream out = this.deflate(baos)) {
            int i;
            while ((i = in.read()) != -1) {
                out.write(i);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof CompressionHelper) {
            CompressionHelper compressionHelper = (CompressionHelper) obj;
            return this.name.equals(compressionHelper.name) && this.level.equals(compressionHelper.level);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", this.name, this.level);
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder<ParamType> {
        @NonNull
        private final String name;
        @NonNull
        private final String level;

        private ParamType params;
        private IOBiFunction<InputStream, ParamType, InputStream> inputStreamWrapper;
        private IOBiFunction<OutputStream, ParamType, OutputStream> outputStreamWrapper;

        private Builder(@NonNull String name) {
            this(name, "Default");
        }

        public Builder<ParamType> setParamsFunc(@NonNull ThrowingSupplier<ParamType> supplier) {
            this.params = supplier.get();
            return this;
        }

        public Builder<ParamType> setInputStreamWrapperSimple(@NonNull IOFunction<InputStream, InputStream> inputStreamWrapper) {
            this.inputStreamWrapper = (in, params) -> inputStreamWrapper.applyThrowing(in);
            return this;
        }

        public Builder<ParamType> setOutputStreamWrapperSimple(@NonNull IOFunction<OutputStream, OutputStream> outputStreamWrapper) {
            this.outputStreamWrapper = (out, params) -> outputStreamWrapper.applyThrowing(out);
            return this;
        }

        public CompressionHelper<ParamType> build() {
            return new CompressionHelper<>(this.name, this.level, this.params, this.inputStreamWrapper, this.outputStreamWrapper);
        }
    }
}
