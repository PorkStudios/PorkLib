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
    }
}
