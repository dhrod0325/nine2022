package ks.util.log;

import ks.commands.gm.GMCommandsUtils;
import ks.model.L1Character;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.util.L1CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

public class L1LogUtils {
    public static boolean LOG_DAMAGE = false;
    public static boolean LOG_SKILL = false;

    private final static Logger logger = LogManager.getLogger();

    private final static Logger gmLogger = LogManager.getLogger("gmLogger");
    private final static Logger enchantLogger = LogManager.getLogger("enchantLogger");
    private final static Logger chatLogger = LogManager.getLogger("chatLogger");
    private final static Logger tradeLogger = LogManager.getLogger("tradeLogger");
    private final static Logger bugCheckLogger = LogManager.getLogger("bugCheckLogger");
    private final static Logger debugLogger = LogManager.getLogger("debugLogger");
    private final static Logger shopLogger = LogManager.getLogger("shopLogger");
    private final static Logger wareHouseLogger = LogManager.getLogger("wareHouseLogger");
    private final static Logger userShopLogger = LogManager.getLogger("userShopLogger");

    public static void gmLog(L1Character character, String msg, Object... args) {
        if (character instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) character;

            if (GMCommandsUtils.isDebug(pc)) {
                Message logMessage = ParameterizedMessageFactory.INSTANCE.newMessage(msg, args);
                String message = logMessage.getFormattedMessage();

                gmLogger.debug(message);

                pc.sendPackets("\\fW" + message);
            }
        }
    }

    public static void debugLog(String s, Object... args) {
        debugLogger.info(s, args);
    }

    public static void bugLog(String msg) {
        bugLog(msg, true);
    }

    public static void bugLog(String msg, boolean sendMsg) {
        bugCheckLogger.info(msg);

        if (sendMsg) {
            L1CommonUtils.sendMessageToAllGm(msg);
        }
    }

    public static void userShopLog(String msg, Object... args) {
        userShopLogger.info(msg, args);
    }

    public static void wareHouseLog(String msg, Object... args) {
        wareHouseLogger.info(msg, args);
    }

    public static void enchantLog(String msg, Object... args) {
        enchantLogger.info(msg, args);
    }

    public static void chatLog(String msg, Object... args) {
        chatLogger.info(msg, args);
    }

    public static void tradeLog(String msg, Object... args) {
        tradeLogger.info(msg, args);
    }

    public static void shopLog(String msg, Object... args) {
        shopLogger.info(msg, args);
    }

    public static String logItemName(L1ItemInstance item) {
        return logItemName(item, item.getCount());
    }

    public static String logItemName(L1ItemInstance item, int count) {
        String name = item.getLogName(count);

        if (item.getBless() == 0) {
            name = "축복받은 " + name;
        } else if (item.getBless() == 2) {
            name = "저주받은 " + name;
        }

        return name;
    }

    public static void skillLog(String msg, Object... args) {
        if (LOG_SKILL) {
            logger.debug(msg, args);
        }
    }

    public static void damageLog(String msg, Object... args) {
        if (LOG_DAMAGE) {
            logger.debug(msg, args);
        }
    }
}
