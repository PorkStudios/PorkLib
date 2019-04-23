package net.daporkchop.lib.logging.format;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.logging.LogLevel;

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
    public String format(@NonNull Date date, String channelName, @NonNull LogLevel level, @NonNull String message) {
        if (channelName == null)    {
            return String.format("[%s] [%s] %s", this.dateFormat.format(date), level.name(), message);
        } else {
            return String.format("[%s] [%s] [%s] %s", this.dateFormat.format(date), channelName, level.name(), message);
        }
    }
}
