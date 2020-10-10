/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package encoding.config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.config.Config;
import net.daporkchop.lib.config.PConfig;
import net.daporkchop.lib.config.decoder.JsonConfigDecoder;
import net.daporkchop.lib.config.util.Element;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * @author DaPorkchop_
 */
public class JsonConfigTest {
    @Test
    public void test() throws IOException   {
        PConfig config = new PConfig(new JsonConfigDecoder());
        try (InputStream in = JsonConfigTest.class.getResourceAsStream("/config.json")) {
            Element.ContainerElement element = new JsonConfigDecoder().decode(in);
            //System.out.println(element.toString());
        }
        try (DataIn in = DataIn.wrap(JsonConfigTest.class.getResourceAsStream("/config.json"))) {
            Root rootInstance = config.load(Root.class, in);
            System.out.println(rootInstance);
            System.out.println(Root.INSTANCe);
        }
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            config.save(Root.INSTANCe, baos);
            System.out.printf("Saved: \n%s\n", new String(baos.toByteArray(), StandardCharsets.UTF_8));
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

        private Root()  { //no-args constructor
            this(null);
        }

        @ToString
        public static class Sub1   {
        }

        @ToString
        public static class Sub11 extends Sub1   {
            @Config.Name("int")
            public int integer;

            @Config.Name("long")
            public long longField;

            public BigInteger biginteger;

            @Config.Name("double")
            public double doubleValue;

            @Config.Name("boolean")
            public boolean aBoolean;

            @Config.Default
            public String testingDefaultNull;

            @Config.Default(
                    objectValue = "encoding.config.TestConstants#randomText"
            )
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
