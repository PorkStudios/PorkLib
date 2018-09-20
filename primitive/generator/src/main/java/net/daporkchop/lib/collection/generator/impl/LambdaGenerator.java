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
import net.daporkchop.lib.collection.generator.Primitive;
import net.daporkchop.lib.collection.generator.PrimitiveGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import static net.daporkchop.lib.collection.generator.PrimitiveGenerator.BASE;

/**
 * @author DaPorkchop_
 */
public class LambdaGenerator extends BaseGenerator {
    @Override
    protected void process(String s, String relativeName, File file) throws IOException {
        boolean needsCheck = true;
        long l = file.lastModified();
        if (s.startsWith("TWOPARAM")) {
            for (Primitive key : Primitive.values()) {
                for (Primitive key2 : Primitive.values()) {
                    for (Primitive value : Primitive.values()) {
                        String s1 = s.replaceAll("_P1_", key.name()).replaceAll("_P2_", key2.name()).replaceAll("_P3_", value.name())
                                .replaceAll("_GH_", key.generic && key2.generic && value.generic ? "<K1 extends " + key.name() + ", K2 extends " + key2.name() + ", V extends " + value.name() + ">" :
                                        key.generic && key2.generic ? "<K1 extends " + key.name() + ", K2 extends " + key2.name() + ">" :
                                                key2.generic && value.generic ? "<K2 extends " + key2.name() + ", V extends " + value.name() + ">" :
                                                        key.generic && value.generic ? "<K1 extends " + key.name() + ", V extends " + value.name() + ">" :
                                                                key.generic ? "<K1 extends " + key.name() + ">" :
                                                                        key2.generic ? "<K2 extends " + key2.name() + ">" :
                                                                                value.generic ? "<V extends " + value.name() + ">" : "")
                                .replaceAll("_p1_", key.generic ? "K1" : key.displayName)
                                .replaceAll("_p2_", key2.generic ? "K2" : key2.displayName)
                                .replaceAll("_p3_", value.generic ? "V" : value.displayName)
                                .replaceAll("_p1e_", key.emptyValue).replaceAll("_p2e_", key2.emptyValue).replaceAll("_p3e_", value.emptyValue)
                                .replaceAll("_p1hf_", key.hashFunction).replaceAll("_p2hf_", value.hashFunction)
                                .replaceFirst("2018-2018", YEAR)
                                .replace("TWOPARAM", "");
                        new File(BASE + relativeName).mkdirs();
                        File o = new File(BASE + relativeName + (file.getName().replace("_P1_", key.name()).replace("_P2_", key2.name()).replace("_P3_", value.name()).replace(".template", ".java")));
                        if (needsCheck && o.exists() && o.lastModified() >= l) {
                            return;
                        } else {
                            needsCheck = false;
                        }
                        FileOutputStream fos = new FileOutputStream(o, false);
                        byte[] b = s1.getBytes(Charset.forName("UTF-8"));
                        fos.write(b);
                        PrimitiveGenerator.FILES++;
                        PrimitiveGenerator.SIZE += b.length;
                        fos.close();
                        if (!o.setLastModified(l)) {
                            throw new IllegalStateException("drai");
                        }
                    }
                }
            }
        } else {
            for (Primitive key : Primitive.values()) {
                for (Primitive value : Primitive.values()) {
                    String s1 = s.replaceAll("_P1_", key.name()).replaceAll("_P2_", value.name())

                            .replaceAll("_GH_", key.generic && value.generic ? "<K extends " + key.name() + ", V extends " + value.name() + ">" :
                                    key.generic ? "<K extends " + key.name() + ">" :
                                            value.generic ? "<V extends " + value.name() + ">" : "")
                            .replaceAll("_GHI_", key.generic && value.generic ? "<K, V>" :
                                    key.generic ? "<K>" : value.generic ? "<V>" : "")
                            .replaceAll("_GKAI_", key.generic ? "Object" : key.displayName)
                            .replaceAll("_GVAI_", value.generic ? "Object" : value.displayName)
                            .replaceAll("_GKC_", key.generic ? " (K)" : "")
                            .replaceAll("_GVC_", value.generic ? " (K)" : "")
                            .replaceAll("_GKG_", key.generic ? "<K>" : "<" + key.name() + ">")
                            .replaceAll("_GKV_", key.generic && value.generic ? "<K, V>" : "")
                            .replaceAll("_GK_", key.generic ? "<K>" : "")
                            .replaceAll("_GV_", value.generic ? "<V>" : "")
                            .replaceAll("_p1_", key.generic ? "K" : key.displayName)
                            .replaceAll("_p2_", value.generic ? "V" : value.displayName)

                            .replaceAll("_p1e_", key.emptyValue).replaceAll("_p2e_", value.emptyValue)
                            .replaceAll("_p1hf_", key.hashFunction).replaceAll("_p2hf_", value.hashFunction)
                            .replaceFirst("2018-2018", YEAR)
                            .replace("KEYONLY", "");
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
                    PrimitiveGenerator.FILES++;
                    PrimitiveGenerator.SIZE += b.length;
                    fos.close();
                    if (!o.setLastModified(l)) {
                        throw new IllegalStateException("drai");
                    }
                    if (s.startsWith("KEYONLY")) {
                        break;
                    }
                }
            }
        }
    }
}
