package ks.commands.gm.command.executor;

import ks.constants.L1SkillIcon;
import ks.constants.L1SkillId;
import ks.model.L1World;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_SkillIconGFX;
import ks.packets.serverpackets.S_SystemMessage;

import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class L1ChatNG implements L1CommandExecutor {
    public static L1CommandExecutor getInstance() {
        return new L1ChatNG();
    }

    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            StringTokenizer st = new StringTokenizer(arg);
            String name = st.nextToken();
            int time = Integer.parseInt(st.nextToken());

            L1PcInstance tg = L1World.getInstance().getPlayer(name);

            if (tg != null) {
                tg.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED, time * 60 * 1000);
                tg.sendPackets(new S_SkillIconGFX(L1SkillIcon.채금, time * 60));
                tg.sendPackets(new S_ServerMessage(286, String.valueOf(time)));
                L1World.getInstance().broadcastServerMessage("게임에 적합하지 않는 행동이기 때문에 " + name + "의 채팅을 " + time + "분간 금지합니다.");
                L1World.getInstance().broadcastServerMessage("받는 대미지 400% 효과가 적용됩니다.");
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " [캐릭터명] [시간(분)] 이라고 입력해 주세요. "));
        }
    }
}
