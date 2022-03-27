package manager.controller;

import com.jfoenix.controls.JFXButton;
import com.sun.management.OperatingSystemMXBean;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import ks.app.config.prop.ServerConfig;
import manager.event.FxLogEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Queue;
import java.util.ResourceBundle;

@Component
public class MonitoringController implements Initializable {
    public final static SimpleDateFormat logTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @FXML
    public TextFlow logArea;
    @FXML
    public ScrollPane logScroll;
    @FXML
    public JFXButton toggleScrollBtn;
    @FXML
    public Text txtMemTotal;
    @FXML
    public Text txtMemFree;
    @FXML
    public Text txtCpu;
    @FXML
    public Text txtThreadCount;
    @FXML
    public Label txtClassCount;
    @FXML
    public Label txtTotalClassCount;
    @FXML
    public Label txtServerName;
    @FXML
    public Label txtServerHost;
    @FXML
    public Label txtServerPort;
    @FXML
    public Label txtMaxUser;

    private boolean toggleScroll = false;

    @Resource(name = "managerTaskScheduler")
    private ThreadPoolTaskScheduler managerTaskScheduler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toggleScroll();

        managerTaskScheduler.scheduleAtFixedRate(this::cpuInfo, Duration.ofMillis(1000));
        managerTaskScheduler.scheduleAtFixedRate(() -> {
            if (toggleScroll && logScroll != null) {
                logScroll.setVvalue(1.0);
            }
        }, Duration.ofMillis(1000));

        serverInfo();

        runTimeInfo();
    }

    private void serverInfo() {
        txtServerName.setText(ServerConfig.SERVER_NAME);
        txtServerHost.setText(ServerConfig.SERVER_HOST_NAME);
        txtServerPort.setText(ServerConfig.SERVER_PORT + "");
        txtMaxUser.setText(ServerConfig.SERVER_MAX_USERS + "");
    }

    private void runTimeInfo() {
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        classLoadingMXBean.getLoadedClassCount();

        txtClassCount.setText(classLoadingMXBean.getLoadedClassCount() + "");
        txtTotalClassCount.setText(classLoadingMXBean.getTotalLoadedClassCount() + "");
    }

    public void cpuInfo() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

            txtCpu.setText(String.format("%.2f", osBean.getSystemCpuLoad() * 100));
            txtMemFree.setText(String.format("%.2f", (double) osBean.getFreePhysicalMemorySize() / 1024 / 1024 / 1024));
            txtMemTotal.setText(String.format("%.2f", (double) osBean.getTotalPhysicalMemorySize() / 1024 / 1024 / 1024));
            txtThreadCount.setText(threadMXBean.getThreadCount() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @EventListener
    public void appendLog(FxLogEvent e) {
        if (logArea == null) {
            return;
        }

        managerTaskScheduler.execute(() -> {
            Queue<FxLogEvent.FxLog> q = e.getSource();

            while (q != null && !q.isEmpty()) {
                FxLogEvent.FxLog log = q.poll();

                long timeMillis = log.timeMillis;
                String level = log.name;
                String message = log.message;

                String time = logTimeFormat.format(new Date(timeMillis));

                Text timeAndLevelText = new Text();
                timeAndLevelText.setText("[" + time + "] [" + level + "] ");

                if ("debug".equalsIgnoreCase(level)) {
                    timeAndLevelText.setStyle("-fx-fill: #375484");
                } else if ("info".equalsIgnoreCase(level)) {
                    timeAndLevelText.setStyle("-fx-fill: #2b6630");
                } else if ("warn".equalsIgnoreCase(level)) {
                    timeAndLevelText.setStyle("-fx-fill: #c97c5a");
                } else if ("error".equalsIgnoreCase(level)) {
                    timeAndLevelText.setStyle("-fx-fill: #ff0000");
                }

                Text messageText = new Text();
                messageText.setText(message);

                Platform.runLater(() -> {
                    logArea.getChildren().add(timeAndLevelText);
                    logArea.getChildren().add(messageText);
                    logArea.getChildren().add(new Text(System.lineSeparator()));
                });

                try {
                    Thread.sleep(100);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });
    }

    public void toggleScroll() {
        toggleScroll = !toggleScroll;

        if (toggleScroll) {
            toggleScrollBtn.setText("자동 스크롤 중지");
        } else {
            toggleScrollBtn.setText("자동 스크롤 시작");
        }
    }
}
