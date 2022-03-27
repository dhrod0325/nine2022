package manager.controller.popup.user;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserPopupController {
    @Resource
    private ApplicationContext applicationContext;

    private UserLabel currentPopupLabel;

    public void setCurrentPopupLabel(UserLabel currentPopupLabel) {
        this.currentPopupLabel = currentPopupLabel;
    }

    @FXML
    public void onClickedCharDesc(MouseEvent e) {
        applicationContext.publishEvent(
                new UserPopupEvent(
                        new UserPopupEvent.UserPopupEventSource(currentPopupLabel.getPc(), UserPopupEvent.EventType.CHAR_DESC)
                )
        );
    }

    @FXML
    public void onClickedCharInventorySearch(MouseEvent e) {
        applicationContext.publishEvent(
                new UserPopupEvent(
                        new UserPopupEvent.UserPopupEventSource(currentPopupLabel.getPc(), UserPopupEvent.EventType.CHAR_INVENTORY_SEARCH)
                )
        );
    }

    @FXML
    public void onClickedCharDrop(MouseEvent e) {
        applicationContext.publishEvent(
                new UserPopupEvent(
                        new UserPopupEvent.UserPopupEventSource(currentPopupLabel.getPc(), UserPopupEvent.EventType.CHAR_DROP)
                )
        );
    }
}