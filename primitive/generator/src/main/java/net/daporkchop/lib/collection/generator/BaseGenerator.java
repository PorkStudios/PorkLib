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

package net.daporkchop.lib.collection.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * @author DaPorkchop_
 */
public abstract class BaseGenerator {
    public static final String YEAR = String.format("2018-%d", Calendar.getInstance().get(Calendar.YEAR));

    public final BaseGenerator generate(File f, String relativeName) throws IOException {
        if (!f.exists() || f.listFiles() == null) {
            throw new IllegalStateException("Unable to find templates! Make sure the working directory is set to PorkLib/primitive/generator/src/main/resources!");
        }
        for (File file : f.listFiles()) {
            if (file.isDirectory()) {
                //System.out.println("Folder " + file.getName());
                generate(file, relativeName + file.getName() + "/");
            } else {
                long length = file.length();
                System.out.println("File " + relativeName + file.getName() + " (size=" + length + " bytes)");
                FileInputStream stream = new FileInputStream(file);
                byte[] contents = new byte[(int) length];
                stream.read(contents);
                stream.close();
                process(new String(contents), relativeName, file);
            }
        }
        return this;
    }

    protected abstract void process(String s, String relativeName, File file) throws IOException;
}
