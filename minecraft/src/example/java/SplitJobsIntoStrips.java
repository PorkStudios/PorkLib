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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.NonNull;
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.common.misc.Tuple;
import net.daporkchop.lib.logging.LogAmount;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.math.vector.i.Vec2i;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@FunctionalInterface
interface JobBuilder {
    Collection<Tuple<String, Set<Vec2i>>> buildJobs(@NonNull Set<Vec2i> in);
}

/**
 * @author DaPorkchop_
 */
public class SplitJobsIntoStrips implements Logging {
    protected static final File SRC = new File("/home/daporkchop/192.168.1.119/Minecraft/2b2t/100k merge #3 (with holes)/missingchunks.json");
    protected static final File DST = new File("/home/daporkchop/Desktop/jobs.zip");

    protected static final JobBuilder AXIS_LINES = in -> {
        Map<Integer, Set<Vec2i>> jobs = new HashMap<>();
        in.forEach(pos -> jobs.computeIfAbsent(pos.getX() >> 3, i -> new HashSet<>()).add(pos));
        return jobs.entrySet().stream()
                .map(entry -> new Tuple<>(String.format("job_%d.json", entry.getKey()), entry.getValue()))
                .collect(Collectors.toSet());
    };

    public static void main(String... args) throws IOException {
        logger.enableANSI().redirectStdOut().setLogAmount(LogAmount.DEBUG);

        Set<Vec2i> positions;
        logger.info("Loading positions...");
        try (Reader reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(SRC)))) {
            positions = StreamSupport.stream(new JsonParser().parse(reader).getAsJsonArray().spliterator(), false)
                    .map(JsonElement::getAsJsonObject)
                    .map(obj -> new Vec2i(obj.get("x").getAsInt(), obj.get("z").getAsInt()))
                    .collect(Collectors.toSet());
        }
        logger.success("%d positions loaded.", positions.size());

        logger.info("Building jobs...");
        JobBuilder builder = AXIS_LINES;
        Collection<Tuple<String, Set<Vec2i>>> jobs = builder.buildJobs(positions);
        positions = null;
        logger.success("%d jobs built.", jobs.size());

        logger.info("Writing jobs...");
        try (ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(DST)))) {
            for (Tuple<String, Set<Vec2i>> tuple : jobs) {
                out.putNextEntry(new ZipEntry(tuple.getA()));
                out.write(tuple.getB().stream()
                        .map(vec -> {
                            JsonObject obj = new JsonObject();
                            obj.addProperty("x", vec.getX());
                            obj.addProperty("z", vec.getY());
                            return obj;
                        })
                        .collect(JsonArray::new, JsonArray::add, JsonArray::addAll)
                        .toString().getBytes(UTF8.utf8));
            }
        }
        logger.success("Jobs written.");
    }
}
