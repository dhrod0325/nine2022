package ks.packets.clientpackets;

import ks.app.config.prop.CodeConfig;
import ks.core.datatables.NpcActionTable;
import ks.core.datatables.npcTalk.NpcTalk;
import ks.core.datatables.npcTalk.NpcTalkTable;
import ks.core.datatables.npc_making.NpcMakingManager;
import ks.core.network.L1Client;
import ks.model.L1Object;
import ks.model.L1Trade;
import ks.model.L1World;
import ks.model.action.xml.L1NpcAction;
import ks.model.action.xml.L1NpcHtml;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.model.rank.L1RankChecker;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.system.portalsystem.model.L1PortalData;
import ks.system.portalsystem.table.PortalSystemTable;
import ks.system.timeDungeon.L1TimeDungeonData;
import ks.system.timeDungeon.L1TimeDungeonTable;
import ks.util.L1CommonUtils;
import ks.util.log.L1LogUtils;

import java.util.Date;

import static ks.constants.L1SkillId.*;

public class C_NPCTalk extends ClientBasePacket {
    public C_NPCTalk(byte[] data, L1Client client) {
        super(data);
        int objectId = readD();

        L1Object npc = L1World.getInstance().findObject(objectId);
        L1PcInstance pc = client.getActiveChar();

        if (pc == null)
            return;

        if (npc == null) {
            return;
        }

        if (L1CommonUtils.isNear(pc.getX(), pc.getY(), npc.getX(), npc.getY(), 3)) {
            return;
        }

        if (pc.getTradeID() != 0) {
            L1Trade.cancel(pc);
        }

        if (npc instanceof L1NpcInstance) {
            L1NpcInstance ni = (L1NpcInstance) npc;
            NpcTalk talk = NpcTalkTable.getInstance().findByNpcId(ni.getNpcId());

            if (talk != null) {
                L1LogUtils.gmLog(pc, "npcId : {} , npcName : {}", ni.getNpcId(), ni.getName());

                NpcTalkTable.getInstance().talk(pc, ni, talk);

                return;
            }
        }

        L1NpcAction action = NpcActionTable.getInstance().get(pc, npc);

        if (action != null) {
            L1NpcHtml html = action.execute("", pc, npc, new byte[0]);

            if (html != null) {
                L1LogUtils.gmLog(pc, "html : {}, npcId : {}", html.getName(), npc.getId());
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), html));
            }

            return;
        }

        if (npc instanceof L1NpcInstance) {
            L1NpcInstance ni = (L1NpcInstance) npc;

            if (ni.getNpcId() == 4208002) {
                if (pc.getLevel() < 52) {
                    pc.sendPackets("기란감옥 입장 최소레벨은 52입니다");
                    return;
                }

                L1TimeDungeonData timeData = L1TimeDungeonTable.getInstance().findByMapId(53, pc.getId(), new Date());

                if (timeData != null) {
                    String msg = "기란던전 잔여 이용시간 : " + timeData.getRemainingMinute() + "분 (초기화 00:00)";

                    pc.sendPackets(msg);
                    pc.sendGreenMessage(msg);
                }
            } else if (ni.getNpcId() == 50034) {
                String msg = "시장검색명령어 일/축/저 아이템명 인첸트,시장 도움말 : .상점";

                pc.sendPackets(msg);
                pc.sendGreenMessage(msg);
            } else if (ni.getNpcId() == 7000066) {
                if (pc.getLevel() < CodeConfig.RANK_BUFF_MIN_LEVEL) {
                    pc.sendPackets("랭커버프는 " + CodeConfig.RANK_BUFF_MIN_LEVEL + "레벨 이상만 이용할 수 있습니다");
                    return;
                }

                if (!pc.getTimer().isTimeOver("rankBuff")) {
                    pc.sendPackets("아직 이용할 수 없습니다");

                    return;
                }

                pc.getTimer().setWaitTime("rankBuff", 3000);

                int rank = L1RankChecker.getInstance().getClassRank(pc);

                if (rank <= 3) {
                    try {
                        L1SkillUtils.skillByGm(pc, PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, DECREASE_WEIGHT, ADVANCE_SPIRIT, IRON_SKIN);

                        pc.sendPackets("신비한 랭커의 힘이 주위를 감싸고 있습니다");
                    } catch (Exception e) {
                        logger.error("오류", e);
                    }
                } else {
                    pc.sendPackets("클래스 랭킹 3위안에 들어야만 랭커버프를 지급받을 수 있습니다");
                }

                return;
            }

            L1PortalData portal = PortalSystemTable.getInstance().findByNpcId(ni.getNpcId());

            if (portal != null) {
                portal.showHtml(pc, ni);
                return;
            }

            if (NpcMakingManager.getInstance().npcTalk(pc, ni)) {
                return;
            }
        }

        npc.onTalkAction(pc);

        if (npc instanceof L1NpcInstance) {
            L1NpcInstance ni = (L1NpcInstance) npc;
            L1LogUtils.gmLog(pc, "npcId : {}, impl : {}", ni.getNpcId(), ni.getTemplate().getImpl());
        }
    }
}
