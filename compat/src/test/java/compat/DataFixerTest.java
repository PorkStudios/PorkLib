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

package compat;

import lombok.NonNull;
import net.daporkchop.lib.common.math.PMath;
import net.daporkchop.lib.compat.datafix.DataCodec;
import net.daporkchop.lib.compat.datafix.DataFixer;
import org.junit.Test;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
public class DataFixerTest {
    @Test
    public void test() {
        DataFixer<Number, String, Integer> fixer = DataFixer.<Number, String, Integer>builder()
                .addCodec(3, new DataCodec<Number, String>() {
                    @Override
                    public Number decode(@NonNull String data) {
                        return Integer.parseInt(data);
                    }

                    @Override
                    public String encode(@NonNull Number value) {
                        return String.valueOf(value.intValue());
                    }
                })
                .addCodec(2, new DataCodec<Number, String>() {
                    @Override
                    public Number decode(@NonNull String data) {
                        return Double.parseDouble(data);
                    }

                    @Override
                    public String encode(@NonNull Number value) {
                        return String.valueOf(value.doubleValue());
                    }
                })
                .addConverter(3, s -> String.valueOf(PMath.floorI(Double.parseDouble(s))))
                .build();

        checkState(fixer.decode("123", 3, 3).intValue() == 123); //simply decodes it
        checkState(fixer.decode("123", 2, 3).intValue() == 123); //converts double to int
        checkState(fixer.decode("123.0", 2, 3).intValue() == 123); //converts double to int
        checkState(fixer.decode("2.2", 2, 3).intValue() == 2); //converts double to int
        checkState(fixer.decode("2.2", 2, 3).doubleValue() == 2.0d); //converts double to int
        checkState(fixer.decode("2.2", 2, 2).doubleValue() == 2.2d); //simply decodes it
        checkState(fixer.decode("2.2", -3, 2).doubleValue() == 2.2d); //simply decodes it
    }
}
