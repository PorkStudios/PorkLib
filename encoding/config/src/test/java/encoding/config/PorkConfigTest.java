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

package encoding.config;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.binary.io.OldDataIn;
import net.daporkchop.lib.config.Config;
import net.daporkchop.lib.config.PConfig;
import net.daporkchop.lib.config.decoder.PorkConfigDecoder;
import net.daporkchop.lib.config.util.Element;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author DaPorkchop_
 */
public class PorkConfigTest {
    @Test
    public void test() throws IOException   {
        PConfig config = new PConfig(new PorkConfigDecoder());
        try (InputStream in = PorkConfigTest.class.getResourceAsStream("/config.cfg")) {
            Element.ContainerElement element = new PorkConfigDecoder().decode(in);
            System.out.println(element.toString());
        }
        try (OldDataIn in = OldDataIn.wrap(PorkConfigTest.class.getResourceAsStream("/config.cfg"))) {
            Root rootInstance = config.load(Root.class, in);
            System.out.println(rootInstance);
            System.out.println(Root.INSTANCe);
        }
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            config.save(Root.INSTANCe, baos);
            System.out.printf("Saved: \n%s\n", new String(baos.toByteArray(), UTF8.utf8));
            try (OldDataIn in = OldDataIn.wrap(new ByteArrayInputStream(baos.toByteArray()))) {
                Root rootInstance = config.load(Root.class, in);
                System.out.println(rootInstance);
                System.out.println(Root.INSTANCe);
            }
        }
    }

    @Config(
            staticInstance = true,
            staticName = "INSTANCe"
    )
    @RequiredArgsConstructor
    @ToString
    private static class Root   {
        public static Object INSTANCe = null;

        @Config.Implementation(Sub11.class)
        @Config.Name("hello world")
        private final Sub1 helloWorld;

        protected Sub2 jef;

        //arrays are not a good thing
        //protected final Sub3 arrays;

        @ToString
        public static class Sub1   {
        }

        @ToString
        public static class Sub11 extends Sub1   {
            @Config.Name("int")
            public int integer;

            @Config.Name("long")
            public long longField;

            public double doubleValue;

            @Config.Name("boolean")
            public boolean aBoolean;

            @Config.Default
            public String testingDefaultNull;

            @Config.Default(
                    objectValue = "encoding.config.TestConstants#randomText"
            )
            @Config.Comment({
                    "This field will be initialized at runtime with the text returned by the",
                    "function specified in the annotation above."
            })
            public String testingDefaultNotNull;
        }

        @ToString
        public static class Sub2    {
            private SubSub1 subentry;

            public static class SubSub1 {
            }
        }

        @AllArgsConstructor
        @ToString
        private static class Sub3   {
            private final int[] intArray;
            private final double[] doubleArray;
            private final String[] stringArray;
        }
    }
}
