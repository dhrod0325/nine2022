package manager.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import manager.controller.popup.user.UserPopupEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class UserInventoryController implements Initializable {

    public TableView inventoryView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @EventListener
    public void onClickedUserPopup(UserPopupEvent e) {
        UserPopupEvent.UserPopupEventSource source = e.getSource();

        if (source.eventType.equals(UserPopupEvent.EventType.CHAR_INVENTORY_SEARCH)) {
            L1PcInstance pc = source.pc;

            List<L1ItemInstance> items = pc.getInventory().getItems();


        }
    }
}
