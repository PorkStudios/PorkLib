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

package net.daporkchop.lib.minecraft.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.minecraft.util.packet.PacketRegistry;

/**
 * All known network protocol versions
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
public enum Protocol {
    BE_v1_6(282, "1.6", Platform.BEDROCK),
    BE_v1_5_3(274, "1.5.3", Platform.BEDROCK),
    BE_v1_4_5(261, "1.4.5", Platform.BEDROCK),
    BE_v1_2_11(201, "1.2.11", Platform.BEDROCK),
    BE_v1_2_9(160, "1.2.9", Platform.BEDROCK),
    BE_v1_2_6(150, "1.2.6", Platform.BEDROCK),
    BE_v1_2_5(141, "1.2.5", Platform.BEDROCK),
    BE_v1_2_3(137, "1.2.3", Platform.BEDROCK),
    JE_v1_13(393, "1.13", Platform.JAVA),
    JE_v1_12_2(340, "1.12.2", Platform.JAVA),
    JE_v1_12_1(338, "1.12.1", Platform.JAVA),
    JE_v1_12(335, "1.12", Platform.JAVA),
    JE_v1_11_2(316, "1.11.2", Platform.JAVA),
    JE_v1_11(315, "1.11", Platform.JAVA),
    JE_v1_10_2(210, "1.10.2", Platform.JAVA);

    private final int networkVersion;

    private final String versionName;

    private final Platform platform;

    public static Protocol getByNetworkVersion(int version, Platform platform) {
        for (Protocol protocol : values()) {
            if (protocol.networkVersion == version) {
                if (platform == null || platform == protocol.platform) {
                    return protocol;
                }
            }
        }
        return null;
    }

    public static Protocol getLatest(@NonNull Platform platform) {
        switch (platform) {
            case BEDROCK:
                return BE_v1_6;
            case JAVA:
                return JE_v1_13;
        }
        throw new IllegalStateException();
    }

    public boolean isLatest() {
        return this == getLatest(this.platform);
    }

    public PacketRegistry getPacketRegistry() {
        return this.platform.getPacketRegistry();
    }
}
