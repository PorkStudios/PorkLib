package net.daporkchop.lib.logging.console.ansi;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.logging.console.Console;
import net.daporkchop.lib.logging.impl.DefaultLogger;

import java.io.PrintStream;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class ANSIConsole implements ANSI, Console {
    @NonNull
    protected final PrintStream printer;

    public ANSIConsole()    {
        this(DefaultLogger.stdOut);
    }

    @Override
    public void setTitle(@NonNull String title) {
        this.printer.printf("%c]0;%s%c", ESC, title, BEL);
    }

    @Override
    public void setTextColor(int color) {
        this.printer.printf("%c[%dm", ESC, color);
    }
}
