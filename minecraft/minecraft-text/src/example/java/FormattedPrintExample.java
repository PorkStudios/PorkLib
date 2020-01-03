/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

import net.daporkchop.lib.minecraft.text.parser.MinecraftFormatParser;

import static net.daporkchop.lib.logging.Logging.*;

/**
 * @author DaPorkchop_
 */
public class FormattedPrintExample {
    public static void main(String... args) {
        logger.setFormatParser(MinecraftFormatParser.getDefaultInstance()).enableANSI();

        logger.info("§9Hello §lWorld§c!");
        logger.info("§9§lTeam §c§lPepsi §r§fNetwork");

        //various text messages to test the json text thingy
        logger.info("{    \"text\": \"foo\",    \"bold\": \"true\",    \"extra\": [    {        \"text\": \"bar\"    },    {        \"text\": \"baz\",        \"bold\": \"false\"    },    {        \"text\": \"qux\",        \"bold\": \"true\"    }]}");
        logger.info("\n2b2t:");
        logger.info("{\"text\":\"\",\"extra\":[{\"text\":\"2B \",\"italic\":true,\"bold\":true,\"color\":\"gray\"},{\"text\":\"A blackhole of destruction into\\n\",\"color\":\"gold\"},{\"text\":\"2T \",\"italic\":true,\"bold\":true,\"color\":\"gray\"},{\"text\":\"a singularity of power\",\"color\":\"gold\"}]}");
        logger.info("\nMineplex:");
        logger.info("{\"text\":\"\",\"extra\":[{\"text\":\" \"},{\"text\":\"---\",\"strikethrough\":true,\"color\":\"blue\"},{\"text\":\"[-\",\"strikethrough\":true,\"bold\":true,\"color\":\"dark_gray\"},{\"text\":\" \",\"color\":\"white\"},{\"text\":\"Mineplex \",\"bold\":true,\"color\":\"gold\"},{\"text\":\"Games \",\"bold\":true,\"color\":\"white\"},{\"text\":\"[\",\"color\":\"dark_gray\"},{\"text\":\"US\",\"color\":\"gray\"},{\"text\":\"] \",\"color\":\"dark_gray\"},{\"text\":\"-]\",\"strikethrough\":true,\"bold\":true,\"color\":\"dark_gray\"},{\"text\":\"---\",\"strikethrough\":true,\"color\":\"blue\"},{\"text\":\"\\n \",\"color\":\"white\"},{\"text\":\"CLANS SEASON 6\",\"bold\":true,\"color\":\"red\"}]}");
    }
}
