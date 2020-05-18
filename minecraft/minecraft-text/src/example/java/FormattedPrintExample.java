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

import net.daporkchop.lib.minecraft.text.MCTextEncoder;
import net.daporkchop.lib.minecraft.text.MCTextType;
import net.daporkchop.lib.minecraft.text.parser.MCFormatParser;

import static net.daporkchop.lib.logging.Logging.*;

/**
 * @author DaPorkchop_
 */
public class FormattedPrintExample {
    private static final String[] MESSAGES = {
            "§9Hello §lWorld§c!",
            "§9§lTeam §c§lPepsi §r§fNetwork",
            "{    \"text\": \"foo\",    \"bold\": \"true\",    \"extra\": [    {        \"text\": \"bar\"    },    {        \"text\": \"baz\",        \"bold\": \"false\"    },    {        \"text\": \"qux\",        \"bold\": \"true\"    }]}",
            "\n2b2t:",
            "{\"text\":\"\",\"extra\":[{\"text\":\"2B \",\"italic\":true,\"bold\":true,\"color\":\"gray\"},{\"text\":\"A blackhole of destruction into\\n\",\"color\":\"gold\"},{\"text\":\"2T \",\"italic\":true,\"bold\":true,\"color\":\"gray\"},{\"text\":\"a singularity of power\",\"color\":\"gold\"}]}",
            "\nMineplex:",
            "{\"text\":\"\",\"extra\":[{\"text\":\" \"},{\"text\":\"---\",\"strikethrough\":true,\"color\":\"blue\"},{\"text\":\"[-\",\"strikethrough\":true,\"bold\":true,\"color\":\"dark_gray\"},{\"text\":\" \",\"color\":\"white\"},{\"text\":\"Mineplex \",\"bold\":true,\"color\":\"gold\"},{\"text\":\"Games \",\"bold\":true,\"color\":\"white\"},{\"text\":\"[\",\"color\":\"dark_gray\"},{\"text\":\"US\",\"color\":\"gray\"},{\"text\":\"] \",\"color\":\"dark_gray\"},{\"text\":\"-]\",\"strikethrough\":true,\"bold\":true,\"color\":\"dark_gray\"},{\"text\":\"---\",\"strikethrough\":true,\"color\":\"blue\"},{\"text\":\"\\n \",\"color\":\"white\"},{\"text\":\"CLANS SEASON 6\",\"bold\":true,\"color\":\"red\"}]}"
    };

    public static void main(String... args) {
        logger.enableANSI();

        logger.info("Displaying legacy text:");
        for (String message : MESSAGES) {
            logger.info(MCTextEncoder.encode(MCTextType.LEGACY, MCFormatParser.DEFAULT.parse(message)));
        }

        logger.setFormatParser(MCFormatParser.DEFAULT)
                .info("\nDisplaying legacy text as formatted messages:");
        for (String message : MESSAGES) {
            logger.info(MCTextEncoder.encode(MCTextType.LEGACY, MCFormatParser.DEFAULT.parse(message)));
        }

        logger.info("\nDisplaying formatted messages:");
        for (String message : MESSAGES) {
            logger.info(message);
        }
    }
}
