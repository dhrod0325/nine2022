package manager.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import ks.app.LineageAppContext;

import java.net.URL;
import java.util.Optional;

public class FxUtils {
    public static void windowChange(URL url, String title) throws Exception {
        windowChange(url, title, -1, -1);
    }

    public static void windowChange(URL url, String title, int width, int height) throws Exception {
        Parent root = FXMLLoader.load(url);
        Stage stage = new Stage();
        Scene scene = new Scene(root, width, height);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public static void alert(String title, String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void confirm(String title, String header, String msg, ConfirmCallBack callback) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(msg);

        Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                callback.callback();
            }
        });
    }

    public static FXMLLoader contextFxmlLoader(URL url) {
        FXMLLoader loader = new FXMLLoader(url);
        loader.setControllerFactory(param -> LineageAppContext.getCtx().getBean(param));

        return loader;
    }

    public interface ConfirmCallBack {
        void callback();
    }
}
