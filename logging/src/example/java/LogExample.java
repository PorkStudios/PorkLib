import net.daporkchop.lib.logging.Logger;
import net.daporkchop.lib.logging.Logging;

/**
 * @author DaPorkchop_
 */
public class LogExample implements Logging {
    public static void main(String... args) {
        logger.info("Hello %2$s!", 89365, "world");
        logger.alert("ALERT!\nYOUR COMPUTER HAVE VIRUS!");
        logger.error("This\nis\na\ntest!");
        logger.alert(new RuntimeException(new NullPointerException("jeff")));
    }
}