package manager.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import ks.app.event.L1ChatEvent;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.C_ChatWhisper;
import manager.util.FxUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ChatController implements Initializable {
    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @FXML
    public JFXButton toggleScrollBtn;
    @FXML
    public ScrollPane chatScroll;
    @FXML
    public TextFlow chatArea;
    @FXML
    public JFXTextField targetNameField;
    @FXML
    public JFXTextField chatField;
    @FXML
    public JFXButton gmToggleScrollBtn;
    @FXML
    public ScrollPane gmChatScroll;
    @FXML
    public TextFlow gmChatArea;

    private final Map<String, List<L1ChatEvent.L1ChatEventSource>> chatMap = new ConcurrentHashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatMap.put("전체", new ArrayList<>());
    }

    public void putChat(L1ChatEvent.L1ChatEventSource source) {
        List<L1ChatEvent.L1ChatEventSource> list = chatMap.get(source.type);

        if (list == null) {
            list = new CopyOnWriteArrayList<>();
        }

        list.add(source);
        chatMap.put(source.type, list);

        List<L1ChatEvent.L1ChatEventSource> allList = chatMap.get("모두");
        if (allList == null) {
            allList = new CopyOnWriteArrayList<>();
        }

        allList.add(source);
        chatMap.put("모두", allList);
    }

    //@EventListener
    public void onEvent(L1ChatEvent event) {
        L1ChatEvent.L1ChatEventSource source = event.getSource();
        putChat(source);
        drawChat(source);
    }

    public void drawChat(L1ChatEvent.L1ChatEventSource source) {
        Text typeText = new Text();
        Text messageText = new Text();

        Platform.runLater(() -> {
            chatArea.getChildren().add(typeText);
            chatArea.getChildren().add(messageText);
            chatArea.getChildren().add(new Text(System.lineSeparator()));
        });

        StringBuilder typeMsg = new StringBuilder();
        typeMsg.append("[").append(source.type).append("]")
                .append(" ")
                .append("[").append(format.format(new Date(source.timeMillis))).append("]")
                .append(" ")
                .append("[").append(source.name).append("]");

        if ("귓말".equalsIgnoreCase(source.type)) {
            typeMsg.append("->").append(" [").append(source.targetName).append("]");
        }

        typeMsg.append(" : ");

        typeText.setText(typeMsg.toString());

        String fill = "#333";

        if ("일반".equalsIgnoreCase(source.type)) {
            fill = "#c97c5a";
        } else if ("귓말".equalsIgnoreCase(source.type)) {
            fill = "#2b6630";
        } else if ("파티".equalsIgnoreCase(source.type)) {
            fill = "#e01d1d";
        } else if ("혈맹".equalsIgnoreCase(source.type)) {
            fill = "#377425";
        } else if ("전체".equalsIgnoreCase(source.type)) {
            fill = "#375484";
        }

        typeText.setStyle("-fx-fill: " + fill);

        messageText.setText(source.message);

        if ((("메티스".equalsIgnoreCase(source.targetName) || "미소피아".equalsIgnoreCase(source.targetName))
                || ("메티스".equalsIgnoreCase(source.name) || "미소피아".equalsIgnoreCase(source.name)))

        ) {
            Text typeTextGm = new Text();
            Text messageTextGm = new Text();

            Platform.runLater(() -> {
                gmChatArea.getChildren().add(typeTextGm);
                gmChatArea.getChildren().add(messageTextGm);
                gmChatArea.getChildren().add(new Text(System.lineSeparator()));
            });

            typeTextGm.setText(typeMsg.toString());
            typeTextGm.setStyle("-fx-fill: " + fill);
            messageTextGm.setText(source.message);
        }
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            String targetName = targetNameField.getText();
            String chatMsg = chatField.getText();

            if (StringUtils.isEmpty(targetName)) {
                L1World.getInstance().broadcastPacketGreenMessage(chatMsg);
                L1World.getInstance().broadcastServerMessage("****** : " + chatMsg);

                onEvent(new L1ChatEvent(new L1ChatEvent.L1ChatEventSource("메티스", "전체", chatMsg)));
            } else {
                L1PcInstance target = L1World.getInstance().getPlayer(targetName);

                if (target == null) {
                    FxUtils.alert("알림", "대상이 없습니다", targetName + "은 게임중이 아닙니다");
                    return;
                }

                L1PcInstance gm = new L1PcInstance();
                gm.setName("메티스");

                C_ChatWhisper.send(gm, target, chatMsg);
            }

            chatField.setText("");
            chatField.requestFocus();
        }
    }

    public void onClickedChat(MouseEvent mouseEvent) {
        Button node = (Button) mouseEvent.getTarget();
        String type = node.getText();

        chatArea.getChildren().clear();
        gmChatArea.getChildren().clear();

        List<L1ChatEvent.L1ChatEventSource> list = chatMap.get(type);

        if (list == null) {
            return;
        }

        for (L1ChatEvent.L1ChatEventSource source : list) {
            drawChat(source);
        }
    }
}
