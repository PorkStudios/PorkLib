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

package net.daporkchop.lib.logging.format;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.logging.LogLevel;
import net.daporkchop.lib.logging.format.component.TextComponent;
import net.daporkchop.lib.logging.format.component.TextComponentHolder;
import net.daporkchop.lib.logging.format.component.TextComponentString;

import java.text.DateFormat;
import java.util.Date;

/**
 * Default implementation of {@link MessageFormatter}. Prints messages as follows:
 * <p>
 * [dd/MM/yyyy HH:mm:ss] [channel] [level] message...
 *
 * @author DaPorkchop_
 */
@Getter
@Setter
@Accessors(chain = true)
public class DefaultMessageFormatter implements MessageFormatter {
    protected DateFormat dateFormat = PorkUtil.DATE_FORMAT;

    @Override
    public TextComponent format(@NonNull Date date, String channelName, @NonNull LogLevel level, @NonNull TextComponent message) {
        String prefix;
        if (channelName == null) {
            prefix = String.format("[%s] [%s] ", this.dateFormat.format(date), level.name());
        } else {
            prefix = String.format("[%s] [%s] [%s] ", this.dateFormat.format(date), channelName, level.name());
        }
        if (message.getText() == null) {
            //if the message has no text, we can safely insert the prefix as the first child without affecting anything
            message.getChildren().add(0, new TextComponentString(prefix));
            return message;
        } else {
            return new TextComponentHolder(new TextComponentString(prefix), message);
        }
    }
}
