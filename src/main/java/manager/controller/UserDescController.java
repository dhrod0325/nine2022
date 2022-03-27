package manager.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import ks.model.pc.L1PcInstance;
import manager.controller.popup.user.UserPopupEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class UserDescController implements Initializable {
    @FXML
    public Label txtCharName;
    @FXML
    public Label txtObjId;
    @FXML
    public Label txtAccountName;
    @FXML
    public Label txtLevel;
    @FXML
    public Label txtHighLevel;
    @FXML
    public Label txtExp;
    @FXML
    public Label txtMaxHp;
    @FXML
    public Label txtCurrentHp;
    @FXML
    public Label txtMaxMp;
    @FXML
    public Label txtCurrentMp;
    @FXML
    public Label txtAc;
    @FXML
    public Label txtWis;
    @FXML
    public Label txtInt;
    @FXML
    public Label txtCha;
    @FXML
    public Label txtDex;
    @FXML
    public Label txtCon;
    @FXML
    public Label txtStr;
    @FXML
    public Label txtTotalInt;
    @FXML
    public Label txtTotalWis;
    @FXML
    public Label txtTotalCha;
    @FXML
    public Label txtTotalCon;
    @FXML
    public Label txtTotalDex;
    @FXML
    public Label txtTotalStr;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @EventListener
    public void onClickedUserPopup(UserPopupEvent e) {
        UserPopupEvent.UserPopupEventSource source = e.getSource();

        if (source.eventType.equals(UserPopupEvent.EventType.CHAR_DESC)) {
            L1PcInstance pc = source.pc;

            txtAccountName.setText(pc.getAccountName());
            txtObjId.setText(pc.getId() + "");
            txtCharName.setText(pc.getName());
            txtLevel.setText(pc.getLevel() + "");
            txtHighLevel.setText(pc.getHighLevel() + "");
            txtExp.setText(pc.getExp() + "");
            txtMaxHp.setText(pc.getMaxHp() + "");
            txtCurrentHp.setText(pc.getCurrentHp() + "");
            txtMaxMp.setText(pc.getMaxMp() + "");
            txtCurrentMp.setText(pc.getCurrentMp() + "");
            txtAc.setText(pc.getAC().getAc() + "");

            txtStr.setText(pc.getAbility().getStr() + "");
            txtDex.setText(pc.getAbility().getDex() + "");
            txtCon.setText(pc.getAbility().getCon() + "");
            txtCha.setText(pc.getAbility().getCha() + "");
            txtInt.setText(pc.getAbility().getInt() + "");
            txtWis.setText(pc.getAbility().getWis() + "");

            txtTotalStr.setText(pc.getAbility().getTotalStr() + "");
            txtTotalDex.setText(pc.getAbility().getTotalDex() + "");
            txtTotalCon.setText(pc.getAbility().getTotalCon() + "");
            txtTotalCha.setText(pc.getAbility().getTotalCha() + "");
            txtTotalInt.setText(pc.getAbility().getTotalInt() + "");
            txtTotalWis.setText(pc.getAbility().getTotalWis() + "");
        }
    }
}
