package manager.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import manager.util.FxUtils;
import org.springframework.stereotype.Component;
import web.config.WebServerConfig;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class WebViewController implements Initializable {
    @FXML
    public WebView webView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WebEngine engine = webView.getEngine();
        engine.setOnAlert(event -> FxUtils.alert("알림", "알림", event.getData()));

        engine.setConfirmHandler(param -> {
            AtomicBoolean result = new AtomicBoolean(false);

            FxUtils.confirm("알림", "알림", param, () -> {
                result.set(true);
            });

            return result.get();
        });

        engine.setCreatePopupHandler(p -> {
            Stage stage = new Stage(StageStyle.UTILITY);
            WebView wv2 = new WebView();
            stage.setScene(new Scene(wv2));
            stage.show();
            return wv2.getEngine();
        });

        engine.load(WebServerConfig.getWebManagerUrl());
    }
}
