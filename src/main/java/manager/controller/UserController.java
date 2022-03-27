package manager.controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import ks.app.event.L1QuitGameEvent;
import ks.app.event.L1SelectCharacterEvent;
import ks.model.pc.L1PcInstance;
import manager.controller.popup.user.UserLabel;
import manager.controller.popup.user.UserPopupController;
import manager.util.FxUtils;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.TaskScheduler;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

//@Component
public class UserController implements Initializable {
    @FXML
    public JFXListView<Label> pcListView;

    private JFXPopup popup;

    @Resource(name = "managerTaskScheduler")
    private TaskScheduler taskScheduler;

    private UserPopupController userPopupController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader loader = FxUtils.contextFxmlLoader(new ClassPathResource("manager/fxml/popup/userSelectPopup.fxml").getURL());
            this.popup = new JFXPopup(loader.load());
            this.userPopupController = loader.getController();

//            taskScheduler.schedule(() -> {
//                for (int i = 0; i < 250; i++) {
//                    L1PcInstance pc = MySqlCharacterStorage.getInstance().loadCharacter("메티스");
//                    C_SelectCharacter.init(pc);
//
//                    UserLabel userLabel = new UserLabel(pc);
//                    addItem(userLabel);
//                }
//            }, Instant.now().plusMillis(2000));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void listViewItemClicked() {
        Label label = pcListView.getSelectionModel().getSelectedItem();

        if (label == null) {
            return;
        }

        popup.show(label, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
        userPopupController.setCurrentPopupLabel((UserLabel) label);
    }

    @EventListener
    public void selectCharacterEvent(L1SelectCharacterEvent e) {
        L1PcInstance pc = e.getSource();
        UserLabel userLabel = new UserLabel(pc);
        addItem(userLabel);
    }

    @EventListener
    public void quitEvent(L1QuitGameEvent e) {
        L1PcInstance pc = e.getSource();
        removeItem(pc);
    }

    public void removeItem(L1PcInstance pc) {
        Platform.runLater(() -> {
            ObservableList<Label> items = pcListView.getItems();

            for (Label label : new ArrayList<>(items)) {
                if (label instanceof UserLabel) {
                    UserLabel userLabel = (UserLabel) label;

                    if (pc.equals(userLabel.getPc())) {
                        pcListView.getItems().remove(userLabel);
                    }
                }
            }

            pcListView.layout();
            pcListView.edit(pcListView.getItems().size() - 1);
        });

    }

    public void addItem(UserLabel userLabel) {
        Platform.runLater(() -> {
            pcListView.getItems().add(userLabel);
            pcListView.layout();
            pcListView.edit(pcListView.getItems().size() - 1);
        });
    }
}
