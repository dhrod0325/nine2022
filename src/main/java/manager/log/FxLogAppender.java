package manager.log;

import ks.app.LineageAppContext;
import ks.app.config.prop.ServerConfig;
import manager.event.FxLogEvent;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.springframework.context.ApplicationContext;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Plugin(name = "FxLogAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
public class FxLogAppender extends AbstractAppender {
    private final Queue<FxLogEvent.FxLog> logEventQueue = new ConcurrentLinkedQueue<>();

    protected FxLogAppender(String name, Filter filter) {
        super(name, filter, null);
    }

    @PluginFactory
    public static FxLogAppender createAppender(@PluginAttribute("name") String name, @PluginElement("Filter") final Filter filter) {
        return new FxLogAppender(name, filter);
    }

    @Override
    public void append(LogEvent event) {
        if (ServerConfig.isTest()) {
            return;
        }

        try {
            String message = event.getMessage().getFormattedMessage();

            logEventQueue.offer(new FxLogEvent.FxLog(event.getLevel().name(), event.getTimeMillis(), message));

            ApplicationContext ctx = LineageAppContext.getCtx();

            if (ctx != null) {
                LineageAppContext.getCtx().publishEvent(new FxLogEvent(logEventQueue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}







