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

package nds;

import net.daporkchop.lib.nds.RomNDS;
import net.daporkchop.lib.nds.header.RomHeadersNDS;
import net.daporkchop.lib.nds.header.RomLanguage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author DaPorkchop_
 */
public class ExampleLoadHeaders {
    public static final File ROM = new File("/home/daporkchop/192.168.1.119/Torrents/ROMs/New Super Mario Bros.nds");

    public static void main(String... args) throws IOException {
        RomNDS rom = new RomNDS(ROM);
        RomHeadersNDS headers = rom.getHeaders();
        System.out.printf("ROM Name: %s\nName length: %d\n", headers.getName(), headers.getName().length());
        System.out.printf("Gamecode: %s\nMakercode: %s\n", headers.getGamecode(), headers.getMakercode());
        System.out.printf("Unitcode: %d\nDSi: %b\n", headers.getUnitcode(), headers.isDSi());
        System.out.printf("Cartridge capacity: %d\n", headers.getDeviceCapacity());
        System.out.printf("Name:\n%s\n%s\n%s\n", headers.getIconTitle().getTitle().getTitle(), headers.getIconTitle().getTitle().getSubtitle(), headers.getIconTitle().getTitle().getManufacturer());

        if (true) {
            BufferedImage img = headers.getIconTitle().getIcon().getAsBufferedImage();
            JFrame frame = new JFrame();
            frame.getContentPane().setLayout(new FlowLayout());
            frame.getContentPane().add(new JLabel(new ImageIcon(img)));
            frame.pack();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }
}
