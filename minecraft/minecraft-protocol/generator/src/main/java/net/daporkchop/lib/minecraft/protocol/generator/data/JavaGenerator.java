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

package net.daporkchop.lib.minecraft.protocol.generator.data;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.minecraft.protocol.generator.ClassWriter;
import net.daporkchop.lib.minecraft.protocol.generator.DataGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author DaPorkchop_
 */
@Getter
public class JavaGenerator implements DataGenerator {
    private final String versionName;
    private final String versionNameDir;
    private final File input;

    public JavaGenerator(@NonNull String versionName, @NonNull File input)  {
        this.versionName = versionName;
        this.input = input;
        this.versionNameDir = this.versionName.replace('.', '_');
    }

    @Override
    public void run(@NonNull File out) throws IOException {
        JsonObject object;
        try (Reader reader = new InputStreamReader(new FileInputStream(this.input))) {
            object = JSON_PARSER.parse(reader).getAsJsonArray().get(0).getAsJsonObject();
        }

        //TODO: my amazing formatter from dev/network-revamp-v6 would be nice here
        try (ClassWriter writer = new ClassWriter(this.ensureFileExists(new File(out, String.format("mc/java/v%s/Java%s.java", this.versionNameDir, this.versionNameDir))), "protocol")) {
            writer
                    .write(String.format("public class Java%s {", this.versionNameDir))
                    .push()
                    .write(String.format("public static final Platform INSTANCE = Platform.DefaultImpl.builder().name(\"Java v%s\").build();", this.versionNameDir))
                    .pop().write("}");
        }
    }
}
