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

import net.daporkchop.lib.logging.LogAmount;
import net.daporkchop.lib.logging.Logger;
import net.daporkchop.lib.logging.Logging;

/**
 * @author DaPorkchop_
 */
public class LogExample implements Logging {
    public static void main(String... args) {
        logger.info("Hello %2$s!", 89365, "world");
        logger.channel("PorkLib").alert("ALERT!\nYOUR COMPUTER HAVE VIRUS!");
        logger.error("This\nis\na\ntest!");
        logger.alert(new RuntimeException(new NullPointerException("jeff")));
        logger.redirectStdOut();
        System.out.println("Test!äöäöä¬");
        System.err.println("Test!äöäöä¬");
        logger.debug("Debug 1");
        logger.setLogAmount(LogAmount.DEBUG);
        logger.debug("Debug 2");

        console.setTextColor(31); //red
        console.setTextColor(0xFF, 0xFF, 0x55);
        logger.info("This text should be red...");
    }
}
