package manager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ks.app.LineageAppContext;
import ks.app.config.prop.ServerConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class FxMain extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(new ClassPathResource("manager/fxml/main.fxml").getURL());
            fxmlLoader.setControllerFactory(param -> LineageAppContext.getCtx().getBean(param));

            Parent root = fxmlLoader.load();

            stage.setTitle(ServerConfig.SERVER_NAME + " 매니저");
            stage.getIcons().add(new Image(new ClassPathResource("images/lineage.png").getPath()));
            stage.setX(0);
            stage.setY(0);

            stage.setScene(new Scene(root));
            stage.getScene().getStylesheets().add(new ClassPathResource("stylesheets/app.css").getURL().toExternalForm());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
    }

}
