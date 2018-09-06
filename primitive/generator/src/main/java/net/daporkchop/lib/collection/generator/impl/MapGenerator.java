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

package net.daporkchop.lib.collection.generator.impl;

import net.daporkchop.lib.collection.generator.BaseGenerator;
import net.daporkchop.lib.collection.generator.CollectionGenerator;
import net.daporkchop.lib.collection.generator.Primitive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import static net.daporkchop.lib.collection.generator.CollectionGenerator.BASE;

/**
 * @author DaPorkchop_
 */
public class MapGenerator extends BaseGenerator {
    @Override
    protected void process(String s, String relativeName, File file) throws IOException {
        long l = file.lastModified();
        boolean needsCheck = true;
        for (Primitive key : Primitive.values()) {
            for (Primitive value : Primitive.values()) {
                String s1 = s
                        .replaceAll("_P1_", key.name()).replaceAll("_P2_", value.name())

                        .replaceAll("_GH_", key.generic && value.generic ? "<K extends " + key.name() + ", V extends " + value.name() + ">" :
                                key.generic ? "<K extends " + key.name() + ">" :
                                        value.generic ? "<V extends " + value.name() + ">" : "")
                        .replaceAll("_GHI_", key.generic && value.generic ? "<K, V>" :
                                key.generic ? "<K>" : value.generic ? "<V>" : "")
                        .replaceAll("_GKAI_", key.generic ? "Object" : key.displayName)
                        .replaceAll("_GVAI_", value.generic ? "Object" : value.displayName)
                        .replaceAll("_GKC_", key.generic ? " (K)" : "")
                        .replaceAll("_GVC_", value.generic ? " (V)" : "")
                        .replaceAll("_GKV_", key.generic && value.generic ? "<K, V>" : "")
                        .replaceAll("_GK_", key.generic ? "<K>" : "")
                        .replaceAll("_GKG_", key.generic ? "<K>" : "<" + key.name() + ">")
                        .replaceAll("_GV_", value.generic ? "<V>" : "")
                        .replaceAll("_p1_", key.generic ? "K" : key.displayName)
                        .replaceAll("_p2_", value.generic ? "V" : value.displayName)

                        .replaceAll("_p1e_", key.emptyValue).replaceAll("_p2e_", value.emptyValue)
                        .replaceAll("_p1hf_", key.hashFunction).replaceAll("_p2hf_", value.hashFunction)
                        .replaceAll("_p1lhf_", key.longHashFunction).replaceAll("_p2lhf_", value.longHashFunction)
                        .replaceAll("_p1b_", key.bytes).replaceAll("_p2b_", value.bytes);
                new File(BASE + relativeName).mkdirs();
                File o = new File(BASE + relativeName + (file.getName().replace("_P1_", key.name()).replace("_P2_", value.name()).replace(".template", ".java")));
                if (needsCheck && o.exists() && o.lastModified() >= l) {
                    return;
                } else {
                    needsCheck = false;
                }
                FileOutputStream fos = new FileOutputStream(o, false);
                byte[] b = s1.getBytes(Charset.forName("UTF-8"));
                fos.write(b);
                CollectionGenerator.FILES++;
                CollectionGenerator.SIZE += b.length;
                fos.close();
                if (!o.setLastModified(l))  {
                    throw new IllegalStateException("drai");
                }
            }
        }
    }
}
