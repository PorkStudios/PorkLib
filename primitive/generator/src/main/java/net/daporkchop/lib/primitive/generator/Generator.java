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

package net.daporkchop.lib.primitive.generator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.UTF8;
import sun.misc.IOUtils;

import java.io.*;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class Generator {
    public static int FILES = 0;
    public static int SIZE = 0;

    @NonNull
    public final File inRoot;

    @NonNull
    public final File outRoot;

    public static void main(String... args) throws IOException {
        /*Generator generator = new Generator(
                new File(".", "primitive/generator/src/main/resources"),
                new File(".", "primitive/src/main/java/net/daporkchop/lib/primitive")
        );*/
        Generator generator = new Generator(
                new File(".", "src/main/resources"),
                new File(".", "../src/main/java/net/daporkchop/lib/primitive")
        );
        generator.generate();

        System.out.println("Generated " + FILES + " files, totalling " + SIZE + " bytes (" + (SIZE / 1024D / 1024D) + " megabytes)");
    }

    public void generate() {
        if (this.outRoot.exists()) {
            this.rmDir(this.outRoot);
        }
        if (!this.outRoot.exists() && !this.outRoot.mkdirs()) {
            throw new IllegalStateException();
        }

        this.generate(this.inRoot, this.outRoot);
    }

    public void generate(@NonNull File file, @NonNull File out) {
        if (!file.exists()) {
            throw new IllegalStateException();
        }
        if (file.isDirectory()) {
            if (!"resources".equals(file.getName())) {
                out = new File(out, file.getName());
            }
            for (File f : file.listFiles()) {
                this.generate(f, out);
            }
        } else if (file.getName().endsWith(".template")) {
            String name = file.getName();
            System.out.println(name);
            int count = Primitive.countVariables(name);
            String content;
            try (InputStream is = new FileInputStream(file)) {
                byte[] b = IOUtils.readFully(is, -1, false);
                content = new String(b);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!out.exists() && !out.mkdirs()) {
                throw new IllegalStateException();
            }
            this.populateToDepth(out, name, content, count);
        }
    }

    private void populateToDepth(@NonNull File path, @NonNull String name, @NonNull String content, int depth, Primitive... primitives) {
        if (depth > primitives.length) {
            Primitive[] p = new Primitive[primitives.length + 1];
            System.arraycopy(primitives, 0, p, 0, primitives.length);
            Primitive.primitives.forEach(primitive -> {
                p[p.length - 1] = primitive;
                this.populateToDepth(path, name, content, depth, p);
            });
        } else {
            String nameOut = name.replaceAll(".template", ".java");
            String contentOut = content;
            if (depth == 0) {
                int i = 0;
                for (Primitive p : Primitive.primitives)    {
                    nameOut = p.format(nameOut, i);
                    contentOut = p.format(contentOut, i++);
                }
            } else {
                for (int i = primitives.length - 1; i >= 0; i--) {
                    Primitive p = primitives[i];
                    nameOut = p.format(nameOut, i);
                    contentOut = p.format(contentOut, i);
                }
            }
            contentOut = contentOut
                    .replaceAll("_gH_", Primitive.getGenericHeader(primitives))
                    .replaceAll("_G_super_", Primitive.getGenericSuper(primitives))
            .replaceAll(Primitive.METHOD_GENERIC_HEADER_DEF, "<V> ");
            File file = new File(path, nameOut);
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] b = contentOut.getBytes(UTF8.utf8);
                os.write(b);
                SIZE += b.length;
                FILES++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void rmDir(@NonNull File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                this.rmDir(f);
            }
        }
        if (!file.delete()) {
            throw new IllegalStateException();
        }
    }
}
