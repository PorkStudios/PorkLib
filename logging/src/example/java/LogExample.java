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

import net.daporkchop.lib.logging.LogAmount;
import net.daporkchop.lib.logging.LogLevel;
import net.daporkchop.lib.logging.Logger;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.logging.console.TextFormat;
import net.daporkchop.lib.logging.format.TextStyle;

import java.awt.Color;
import java.io.File;

import static net.daporkchop.lib.logging.Logging.*;

/**
 * @author DaPorkchop_
 */
public class LogExample {
    public static void main(String... args) {
        logger.enableANSI().addFile(new File("./test_out/log_example.log"), LogAmount.DEBUG);

        System.out.println("stdout before logger override");
        System.err.println("stderr before logger override");
        logger.redirectStdOut();
        System.out.println("stdout after logger override");
        System.err.println("stderr after logger override");

        logger.info("Hello %2$s!", 89365, "world");
        logger.alert("ALERT!\nYOUR COMPUTER HAVE VIRUS!");
        logger.error("This\nis\na\ntest!");
        logger.alert(new RuntimeException(new NullPointerException("jeff")));
        logger.redirectStdOut();
        System.out.println("Test!äöäöä¬");
        System.err.println("Test!äöäöä¬");
        logger.debug("Debug 1");
        logger.setLogAmount(LogAmount.DEBUG);
        logger.debug("Debug 2");

        for (LogLevel level : LogLevel.values())    {
            logger.channel("PorkLib").log(level, level.name());
        }

        if (false) {
            //you really shouldn't be directly interacting with the console class...
            console.setTextColor(Color.BLUE);
            logger.info("This text should be blue...");
            console.setBackgroundColor(Color.ORANGE);
            logger.info("With an orange background...");
            console.setBold(true);
            console.setBlinking(true);
            logger.info("In bold and blinking!");
            console.setBold(false);
            logger.info("Wait, sorry. This isn't bold!");
        }
    }
}
