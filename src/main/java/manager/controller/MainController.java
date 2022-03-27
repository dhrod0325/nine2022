package manager.controller;

import javafx.fxml.FXML;
import ks.core.GameServer;
import ks.util.L1ServerUtils;
import manager.util.FxUtils;
import org.springframework.stereotype.Component;

@Component
public class MainController {
    @FXML
    public void closeButtonAction() {
        FxUtils.confirm("알림", "서버종료", "정말로 서버를 종료하시겠습니까?", () -> {
            GameServer.getInstance().shutdownWithCountdown(20);
        });
    }

    @FXML
    public void closeNowButtonAction() {
        GameServer.getInstance().shutdown();
    }

    @FXML
    public void initServer() {
        FxUtils.confirm("알림", "서버초기화", "정말로 서버를 초기화 하시겠습니까?", () -> {
            L1ServerUtils.getInstance().init();
            GameServer.getInstance().shutdownWithCountdown(0);
        });
    }
}
