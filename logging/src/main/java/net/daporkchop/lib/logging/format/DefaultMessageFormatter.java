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

package net.daporkchop.lib.logging.format;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.logging.LogLevel;
import net.daporkchop.lib.logging.console.TextFormat;
import net.daporkchop.lib.logging.format.component.TextComponent;
import net.daporkchop.lib.logging.format.component.TextComponentHolder;
import net.daporkchop.lib.logging.format.component.TextComponentString;

import java.awt.Color;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
    protected static final TextComponent START = new TextComponentString("[");
    protected static final TextComponent BETWEEN = new TextComponentString("] [");
    protected static final TextComponent END = new TextComponentString("] ");

    protected static final Map<LogLevel, TextComponent> LEVEL_COMPONENTS = new EnumMap<>(LogLevel.class);
    protected static final TextFormat DATE_STYLE = new TextFormat().setTextColor(Color.CYAN);

    static {
        for (LogLevel level : LogLevel.values())    {
            LEVEL_COMPONENTS.put(level, new TextComponentString(level.getColor(), null, level.getStyle(), level.name()));
        }
    }

    protected DateFormat dateFormat = PorkUtil.DATE_FORMAT;

    @Override
    public TextComponent format(@NonNull Date date, String channelName, @NonNull LogLevel level, @NonNull TextComponent message) {
        List<TextComponent> components = new ArrayList<>((channelName == null ? 5 : 7) + (message.getText() == null ? 0 : 1) + message.getChildren().size());

        components.add(START);
        components.add(new TextComponentString(DATE_STYLE, this.dateFormat.format(date)));
        components.add(BETWEEN);
        if (channelName != null)    {
            components.add(new TextComponentString(channelName));
            components.add(BETWEEN);
        }
        components.add(LEVEL_COMPONENTS.get(level));
        components.add(END);
        if (message.getText() != null)  {
            components.add(message.getChildren().isEmpty() ? message : new TextComponentString(message.getColor(), message.getBackgroundColor(), message.getStyle(), message.getText()));
        }
        components.addAll(message.getChildren());

        return new TextComponentHolder(components);
    }
}
